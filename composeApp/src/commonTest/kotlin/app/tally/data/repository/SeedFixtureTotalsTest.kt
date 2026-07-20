package app.tally.data.repository

import app.tally.domain.usecase.computeTotals
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Phase 1 acceptance: `ComputeTotals` over the seed fixture matches the kit's
 * shown values **except** the monthly total, which is correctly lower because
 * yearly-billed subs get normalized (there are none in this all-monthly seed
 * set, so this test's real job is pinning the *count*-based values, which
 * should match the kit exactly since counts aren't cadence-sensitive).
 */
class SeedFixtureTotalsTest {

    @Test
    fun `seed fixture matches the kit's displayed active count and hero total`() {
        val today = LocalDate(2026, 1, 1)
        val subs = seedSubscriptions(today)
        val result = computeTotals(subs, activeCurrency = "USD", today = today)

        // Kit: 9 subs total, 8 active (Peloton paused) -> "across 8 active subscriptions".
        assertEquals(8, result.activeCount)

        // All nine are MONTHLY, so no yearly÷12 normalization applies here — the kit's
        // naive Σ price and this cadence-normalized sum are numerically identical for
        // this particular seed set. The divergence (documented in ComputeTotalsTest's
        // "yearly is normalized" test) only shows up once a yearly sub exists.
        val kitNaiveMonthlySum = 1549 + 1199 + 299 + 800 + 1399 + 425 + 1399 + 2000 // excludes paused Peloton
        assertEquals(kitNaiveMonthlySum.toLong(), result.monthlyTotalMinor)

        // Kit: Netflix(3d,due) + Spotify(5d,due) + iCloud+(6d,due) + ChatGPT(2d,trial) <= 6 days.
        assertEquals(4, result.renewingSoon.size)

        // Kit: same four plus none extra land in <= 7 (no sub sits at exactly 7).
        assertEquals(4, result.upcomingCount)
    }
}
