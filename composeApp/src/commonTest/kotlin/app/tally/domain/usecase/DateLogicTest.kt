package app.tally.domain.usecase

import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Status
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DaysUntilTest {
    @Test
    fun `paused always returns the 99 sentinel regardless of date`() {
        assertEquals(99, daysUntil(LocalDate(2026, 1, 1), Status.PAUSED, LocalDate(2026, 6, 1)))
        assertEquals(99, daysUntil(LocalDate(2099, 1, 1), Status.PAUSED, LocalDate(2026, 6, 1)))
    }

    @Test
    fun `non-paused returns the real day difference`() {
        assertEquals(7, daysUntil(LocalDate(2026, 1, 8), Status.DUE, LocalDate(2026, 1, 1)))
        assertEquals(-3, daysUntil(LocalDate(2026, 1, 1), Status.OVERDUE, LocalDate(2026, 1, 4)))
    }
}

class DeriveDueTextTest {
    private val today = LocalDate(2026, 1, 1)

    @Test
    fun `paused is always Paused regardless of date`() {
        assertEquals(DueTextKind.Paused, deriveDueTextKind(LocalDate(2020, 1, 1), Status.PAUSED, today))
    }

    @Test
    fun `trial always renders as TrialEndsIn, even at 0 or 1 days`() {
        assertEquals(DueTextKind.TrialEndsIn(0), deriveDueTextKind(today, Status.TRIAL, today))
        assertEquals(DueTextKind.TrialEndsIn(1), deriveDueTextKind(LocalDate(2026, 1, 2), Status.TRIAL, today))
        assertEquals(DueTextKind.TrialEndsIn(2), deriveDueTextKind(LocalDate(2026, 1, 3), Status.TRIAL, today))
    }

    @Test
    fun `today, tomorrow, and n-days-out for non-trial statuses`() {
        assertEquals(DueTextKind.Today, deriveDueTextKind(today, Status.DUE, today))
        assertEquals(DueTextKind.Tomorrow, deriveDueTextKind(LocalDate(2026, 1, 2), Status.DUE, today))
        assertEquals(DueTextKind.InDays(3), deriveDueTextKind(LocalDate(2026, 1, 4), Status.ACTIVE, today))
    }

    @Test
    fun `past dates render as OverdueBy with a positive day count`() {
        assertEquals(DueTextKind.OverdueBy(5), deriveDueTextKind(LocalDate(2025, 12, 27), Status.OVERDUE, today))
    }
}

class AdvanceBillingDateTest {

    @Test
    fun `monthly Jan 31 anchors through Feb into Mar 31, not drifting to Mar 28`() {
        val firstBilling = LocalDate(2026, 1, 31)
        // 2026 is not a leap year: Jan 31 -> Feb 28 -> Mar 31.
        val afterOneCycle = advanceBillingDate(firstBilling, firstBilling, BillingCycle.MONTHLY, today = LocalDate(2026, 2, 1))
        assertEquals(LocalDate(2026, 2, 28), afterOneCycle)

        val afterTwoCycles = advanceBillingDate(afterOneCycle, firstBilling, BillingCycle.MONTHLY, today = LocalDate(2026, 3, 1))
        assertEquals(LocalDate(2026, 3, 31), afterTwoCycles) // anchored back to 31, not drifted to 28
    }

    @Test
    fun `monthly Jan 31 in a leap year goes through Feb 29`() {
        val firstBilling = LocalDate(2028, 1, 31) // 2028 is a leap year
        val afterOneCycle = advanceBillingDate(firstBilling, firstBilling, BillingCycle.MONTHLY, today = LocalDate(2028, 2, 1))
        assertEquals(LocalDate(2028, 2, 29), afterOneCycle)

        val afterTwoCycles = advanceBillingDate(afterOneCycle, firstBilling, BillingCycle.MONTHLY, today = LocalDate(2028, 3, 1))
        assertEquals(LocalDate(2028, 3, 31), afterTwoCycles)
    }

    @Test
    fun `yearly Feb 29 rolls to Feb 28 in the next non-leap year`() {
        val firstBilling = LocalDate(2028, 2, 29) // leap year
        val next = advanceBillingDate(firstBilling, firstBilling, BillingCycle.YEARLY, today = LocalDate(2029, 1, 1))
        assertEquals(LocalDate(2029, 2, 28), next) // 2029 is not a leap year
    }

    @Test
    fun `yearly Feb 29 returns to Feb 29 once a leap year comes around again`() {
        // 2028 and 2032 are both leap years; 2029-2031 aren't. One call catches up
        // all four missed cycles (2029-02-28, 2030-02-28, 2031-02-28, 2032-02-29).
        val firstBilling = LocalDate(2028, 2, 29)
        val next = advanceBillingDate(firstBilling, firstBilling, BillingCycle.YEARLY, today = LocalDate(2032, 2, 29))
        assertEquals(LocalDate(2032, 2, 29), next)
    }

    @Test
    fun `catches up multiple missed cycles in one call`() {
        val firstBilling = LocalDate(2026, 1, 15)
        val next = advanceBillingDate(firstBilling, firstBilling, BillingCycle.MONTHLY, today = LocalDate(2026, 5, 1))
        assertEquals(LocalDate(2026, 5, 15), next)
    }

    @Test
    fun `weekly advances by exactly 7 days per cycle`() {
        val firstBilling = LocalDate(2026, 1, 1)
        val next = advanceBillingDate(firstBilling, firstBilling, BillingCycle.WEEKLY, today = LocalDate(2026, 1, 10))
        assertEquals(LocalDate(2026, 1, 15), next)
    }

    @Test
    fun `quarterly is also anchored to the first billing day-of-month`() {
        val firstBilling = LocalDate(2026, 11, 30)
        val next = advanceBillingDate(firstBilling, firstBilling, BillingCycle.QUARTERLY, today = LocalDate(2027, 2, 1))
        assertEquals(LocalDate(2027, 2, 28), next) // Nov 30 + 3 months = Feb 28/29, non-leap here
    }

    @Test
    fun `a date that already equals today does not advance`() {
        val today = LocalDate(2026, 1, 1)
        assertEquals(today, advanceBillingDate(today, today, BillingCycle.MONTHLY, today = today))
    }
}
