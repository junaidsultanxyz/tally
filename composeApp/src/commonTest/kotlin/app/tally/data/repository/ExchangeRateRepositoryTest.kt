package app.tally.data.repository

import app.tally.data.local.ExchangeRateDao
import app.tally.data.local.ExchangeRateEntity
import app.tally.data.remote.ExchangeRateSource
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

private class FakeExchangeRateDao : ExchangeRateDao {
    private val store = mutableMapOf<Pair<String, String>, ExchangeRateEntity>()

    override suspend fun upsertAll(rates: List<ExchangeRateEntity>) {
        rates.forEach { store[it.baseCurrency to it.targetCurrency] = it }
    }

    override suspend fun getRate(base: String, target: String): ExchangeRateEntity? = store[base to target]

    override suspend fun getFreshnessMarker(base: String): ExchangeRateEntity? = store[base to base]

    fun seed(entity: ExchangeRateEntity) {
        store[entity.baseCurrency to entity.targetCurrency] = entity
    }
}

private class FakeExchangeRateSource(
    private val ratesByBase: Map<String, Map<String, Double>> = emptyMap(),
    private val throwOnFetch: Boolean = false,
) : ExchangeRateSource {
    var fetchCount: Int = 0
        private set

    override suspend fun fetchRates(baseCurrency: String): Map<String, Double> {
        fetchCount++
        if (throwOnFetch) throw RuntimeException("network unavailable")
        return ratesByBase[baseCurrency] ?: emptyMap()
    }
}

class ExchangeRateRepositoryTest {

    @Test
    fun `same currency always returns 1_0 without touching the source`() = runBlocking {
        val source = FakeExchangeRateSource()
        val repository = ExchangeRateRepository(source, FakeExchangeRateDao())

        assertEquals(1.0, repository.getRate("USD", "USD"))
        assertEquals(0, source.fetchCount)
    }

    @Test
    fun `fetches and caches on first use, then reuses the cache within the TTL`() = runBlocking {
        val source = FakeExchangeRateSource(ratesByBase = mapOf("USD" to mapOf("PKR" to 278.5, "EUR" to 0.92)))
        val repository = ExchangeRateRepository(source, FakeExchangeRateDao())

        assertEquals(278.5, repository.getRate("USD", "PKR"))
        assertEquals(0.92, repository.getRate("USD", "EUR"))
        assertEquals(1, source.fetchCount) // one fetch covers every target currency for that base

        repository.getRate("USD", "PKR") // still within the TTL
        assertEquals(1, source.fetchCount) // no second network call
    }

    @Test
    fun `a fetch failure with nothing cached yet returns null, not an exception`() = runBlocking {
        val source = FakeExchangeRateSource(throwOnFetch = true)
        val repository = ExchangeRateRepository(source, FakeExchangeRateDao())

        assertNull(repository.getRate("USD", "PKR"))
    }

    @Test
    fun `a fetch failure with a stale cache falls back to the stale rate rather than losing it`() = runBlocking {
        val dao = FakeExchangeRateDao()
        val old = Clock.System.now() - 48.hours
        dao.seed(ExchangeRateEntity("USD", "USD", 1.0, old))
        dao.seed(ExchangeRateEntity("USD", "PKR", 275.0, old))
        val source = FakeExchangeRateSource(throwOnFetch = true)
        val repository = ExchangeRateRepository(source, dao)

        assertEquals(275.0, repository.getRate("USD", "PKR"))
        assertEquals(1, source.fetchCount) // it did try, since the cache was stale
    }

    @Test
    fun `a fresh cache is used as-is without ever calling the source`() = runBlocking {
        val dao = FakeExchangeRateDao()
        val recent = Clock.System.now() - 1.hours
        dao.seed(ExchangeRateEntity("USD", "USD", 1.0, recent))
        dao.seed(ExchangeRateEntity("USD", "PKR", 280.0, recent))
        val source = FakeExchangeRateSource(ratesByBase = mapOf("USD" to mapOf("PKR" to 999.0)))
        val repository = ExchangeRateRepository(source, dao)

        assertEquals(280.0, repository.getRate("USD", "PKR"))
        assertEquals(0, source.fetchCount)
    }
}
