package app.tally.data.repository

import app.tally.data.local.ExchangeRateDao
import app.tally.data.local.ExchangeRateEntity
import app.tally.data.remote.ExchangeRateSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

private val CACHE_TTL = 12.hours

/**
 * Currency conversion is a **best-effort enhancement, never a blocker** (AGENTS.md §12
 * offline-first): [getRate] returns `null` — not a thrown exception — whenever a rate
 * genuinely isn't available (offline on first use, or an API failure), and callers
 * (`computeTotals`) are expected to just skip converting that one currency rather than
 * failing the whole totals computation.
 *
 * One HTTP call fetches every target currency's rate against a base in one shot, cached in
 * Room for [CACHE_TTL] — cheap enough that "efficient" doesn't need anything cleverer than a
 * plain TTL, and a [Mutex] collapses concurrent callers (e.g. several dashboard recompositions
 * in a burst) into a single in-flight fetch per base instead of one each.
 */
public class ExchangeRateRepository(
    private val api: ExchangeRateSource,
    private val dao: ExchangeRateDao,
) {
    private val mutex = Mutex()

    public suspend fun getRate(from: String, to: String): Double? {
        if (from == to) return 1.0
        ensureFreshRates(from)
        return dao.getRate(from, to)?.rate
    }

    private suspend fun ensureFreshRates(base: String): Unit = mutex.withLock {
        val marker = dao.getFreshnessMarker(base)
        val isFresh = marker != null && (Clock.System.now() - marker.fetchedAt) < CACHE_TTL
        if (isFresh) return@withLock

        val rates = try {
            api.fetchRates(base)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Offline, timed out, or the API had a bad day — keep whatever's already cached
            // (possibly stale, possibly nothing at all) rather than surfacing this upward.
            return@withLock
        }
        if (rates.isEmpty()) return@withLock

        val now = Clock.System.now()
        val entities = buildList {
            add(ExchangeRateEntity(base, base, 1.0, now))
            rates.forEach { (target, rate) -> if (target != base) add(ExchangeRateEntity(base, target, rate, now)) }
        }
        dao.upsertAll(entities)
    }
}
