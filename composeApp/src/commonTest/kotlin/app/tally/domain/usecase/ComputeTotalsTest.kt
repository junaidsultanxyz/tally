package app.tally.domain.usecase

import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

private val TODAY = LocalDate(2026, 1, 1)

private fun sub(
    amountMinor: Long,
    billingCycle: BillingCycle = BillingCycle.MONTHLY,
    status: Status = Status.ACTIVE,
    nextBillingDate: LocalDate = TODAY,
    currencyCode: String = "USD",
): Subscription = Subscription(
    id = "id-$amountMinor-$billingCycle-$nextBillingDate-${status}",
    name = "Test",
    category = Category.OTHER,
    amountMinor = amountMinor,
    currencyCode = currencyCode,
    billingCycle = billingCycle,
    firstBillingDate = nextBillingDate,
    nextBillingDate = nextBillingDate,
    status = status,
    reminderTime = LocalTime(9, 0),
    updatedAt = Instant.fromEpochMilliseconds(0),
)

class ComputeTotalsTest {

    @Test
    fun `monthly contributes as-is`() {
        val result = computeTotals(listOf(sub(1500, BillingCycle.MONTHLY)), "USD", TODAY)
        assertEquals(1500L, result.monthlyTotalMinor)
    }

    @Test
    fun `yearly is normalized to one-twelfth, correcting the kit's naive sum`() {
        // A $12.00/yr sub: the kit's naive approach would sum the sticker price ($12.00)
        // straight into the monthly total; the correct cadence-normalized contribution is
        // 1200/12 = 100 minor units ($1.00/mo). Recording both numbers documents the
        // intentional divergence from the kit (AGENTS.md §11).
        val kitNaiveSum = 1200L
        val result = computeTotals(listOf(sub(1200, BillingCycle.YEARLY)), "USD", TODAY)
        assertEquals(100L, result.monthlyTotalMinor)
        assert(result.monthlyTotalMinor < kitNaiveSum) { "normalized total must be lower than the kit's naive sum" }
    }

    @Test
    fun `weekly is times 52 divided by 12`() {
        // $10.00/week -> 1000 * 52 / 12 = 4333.33... -> rounds to 4333 minor units.
        val result = computeTotals(listOf(sub(1000, BillingCycle.WEEKLY)), "USD", TODAY)
        assertEquals(4333L, result.monthlyTotalMinor)
    }

    @Test
    fun `quarterly is divided by 3`() {
        val result = computeTotals(listOf(sub(3000, BillingCycle.QUARTERLY)), "USD", TODAY)
        assertEquals(1000L, result.monthlyTotalMinor)
    }

    @Test
    fun `paused subscriptions are excluded from every total and count`() {
        val subs = listOf(sub(10000, BillingCycle.MONTHLY, status = Status.PAUSED))
        val result = computeTotals(subs, "USD", TODAY)
        assertEquals(0L, result.monthlyTotalMinor)
        assertEquals(0, result.activeCount)
        assertEquals(0, result.upcomingCount)
        assertEquals(emptyList(), result.renewingSoon)
    }

    @Test
    fun `rounding happens once on the summed total, not per item, to avoid drift`() {
        // Three yearly subs at 1 cent each: naive per-item rounding of 1/12 = 0.08 -> 0
        // each (rounds down since <0.5), giving a wrong total of 0. Correct: the scaled
        // sum (1+1+1)=3, rounded once as 3/12 = 0.25 -> 0. Same answer here, so use a
        // case where per-item rounding would visibly diverge from summed rounding:
        // three subs at 6 cents/year each. Per-item: 6/12=0.5 -> rounds to 1 each -> 3
        // total. Summed-then-rounded (correct): (6+6+6)/12 = 18/12 = 1.5 -> rounds to 2.
        val subs = List(3) { sub(6, BillingCycle.YEARLY) }
        val result = computeTotals(subs, "USD", TODAY)
        assertEquals(2L, result.monthlyTotalMinor) // not 3, which per-item rounding would wrongly give
    }

    @Test
    fun `upcomingCount threshold is days less than or equal to 7`() {
        val at7 = sub(100, nextBillingDate = TODAY.plusDays(7))
        val at8 = sub(100, nextBillingDate = TODAY.plusDays(8))
        assertEquals(1, computeTotals(listOf(at7), "USD", TODAY).upcomingCount)
        assertEquals(0, computeTotals(listOf(at8), "USD", TODAY).upcomingCount)
    }

    @Test
    fun `renewingSoon threshold is days less than or equal to 6`() {
        val at6 = sub(100, nextBillingDate = TODAY.plusDays(6))
        val at7 = sub(100, nextBillingDate = TODAY.plusDays(7))
        assertEquals(1, computeTotals(listOf(at6), "USD", TODAY).renewingSoon.size)
        assertEquals(0, computeTotals(listOf(at7), "USD", TODAY).renewingSoon.size)
    }

    @Test
    fun `only the active currency contributes to the primary totals, others land in the breakdown`() {
        val usd = sub(1000, currencyCode = "USD")
        val eur = sub(2000, currencyCode = "EUR")
        val result = computeTotals(listOf(usd, eur), "USD", TODAY)
        assertEquals(1000L, result.monthlyTotalMinor)
        assertEquals(1000L, result.perCurrencyBreakdown["USD"])
        assertEquals(2000L, result.perCurrencyBreakdown["EUR"])
    }

    @Test
    fun `annual total is monthly times 12`() {
        val result = computeTotals(listOf(sub(1000, BillingCycle.MONTHLY)), "USD", TODAY)
        assertEquals(12000L, result.annualTotalMinor)
    }

    @Test
    fun `a foreign-currency sub converts into the total when a rate is supplied`() {
        // €20.00 at 1 EUR = 1.08 USD -> $21.60, on top of a $10.00 USD sub -> $31.60 total.
        val usd = sub(1000, currencyCode = "USD")
        val eur = sub(2000, currencyCode = "EUR")
        val result = computeTotals(listOf(usd, eur), "USD", TODAY, ratesToActiveCurrency = mapOf("EUR" to 1.08))
        assertEquals(3160L, result.monthlyTotalMinor)
        // perCurrencyBreakdown stays native/unconverted regardless.
        assertEquals(1000L, result.perCurrencyBreakdown["USD"])
        assertEquals(2000L, result.perCurrencyBreakdown["EUR"])
    }

    @Test
    fun `a foreign-currency sub with no available rate is excluded, same as before conversion existed`() {
        val usd = sub(1000, currencyCode = "USD")
        val eur = sub(2000, currencyCode = "EUR")
        val result = computeTotals(listOf(usd, eur), "USD", TODAY, ratesToActiveCurrency = emptyMap())
        assertEquals(1000L, result.monthlyTotalMinor)
    }

    @Test
    fun `converted yearly subs still normalize to a monthly-equivalent before summing`() {
        // ¥12,000/yr at 1 JPY = 0.0067 USD -> $80.40/yr equivalent -> $6.70/mo (670 minor units).
        val jpyYearly = sub(12000, BillingCycle.YEARLY, currencyCode = "JPY")
        val result = computeTotals(listOf(jpyYearly), "USD", TODAY, ratesToActiveCurrency = mapOf("JPY" to 0.0067))
        assertEquals(670L, result.monthlyTotalMinor)
    }

    @Test
    fun `converting many small foreign amounts still rounds once at the end, not per item`() {
        // Ten PKR subs, each contributing a fractional cent after conversion — summed before
        // rounding, not rounded individually and then summed (which would drift to 0).
        val rate = 0.0036 // 1 PKR ~ 0.36 cents USD
        val subs = List(10) { sub(100, currencyCode = "PKR") } // 100 minor PKR each = ~0.36 cents USD each converted
        val result = computeTotals(subs, "USD", TODAY, ratesToActiveCurrency = mapOf("PKR" to rate))
        // 10 * (100 * 0.0036) = 3.6 minor USD units -> rounds to 4, not 0 (which per-item flooring would give).
        assertEquals(4L, result.monthlyTotalMinor)
    }
}

private fun LocalDate.plusDays(days: Int): LocalDate = LocalDate.fromEpochDays(this.toEpochDays() + days)
