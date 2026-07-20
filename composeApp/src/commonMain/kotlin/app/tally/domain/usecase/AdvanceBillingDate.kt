package app.tally.domain.usecase

import app.tally.domain.model.BillingCycle
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.plus

/**
 * Rolls [current] (a subscription's `nextBillingDate`) forward, one cycle at
 * a time, until it's no longer strictly before [today] — handling however
 * many cycles were missed if the app wasn't opened for a while. Every
 * monthly/quarterly/yearly step re-derives the target day from
 * [firstBillingDate]'s **original** day-of-month, clamped to the target
 * month's real length — never from `current`'s (possibly already-clamped)
 * day. That anchoring is what makes Jan 31 → Feb 28 → Mar 31 correct instead
 * of drifting to Mar 28 (AGENTS.md §8).
 */
public fun advanceBillingDate(
    current: LocalDate,
    firstBillingDate: LocalDate,
    billingCycle: BillingCycle,
    today: LocalDate,
): LocalDate {
    var next = current
    while (next < today) {
        next = when (billingCycle) {
            BillingCycle.MONTHLY -> addMonthsAnchored(next, monthsToAdd = 1, anchorDay = firstBillingDate.day)
            BillingCycle.QUARTERLY -> addMonthsAnchored(next, monthsToAdd = 3, anchorDay = firstBillingDate.day)
            BillingCycle.YEARLY -> addMonthsAnchored(next, monthsToAdd = 12, anchorDay = firstBillingDate.day)
            BillingCycle.WEEKLY -> next.plus(DatePeriod(days = 7))
            BillingCycle.CUSTOM -> error(
                "advanceBillingDate: BillingCycle.CUSTOM has no defined interval yet — " +
                    "the domain model has no interval field for it (BillingCycle.kt). " +
                    "Not implemented rather than guessed.",
            )
        }
    }
    return next
}

private fun addMonthsAnchored(current: LocalDate, monthsToAdd: Int, anchorDay: Int): LocalDate {
    val totalMonthsSinceEpoch = current.year * 12 + (current.monthNumber - 1) + monthsToAdd
    val targetYear = totalMonthsSinceEpoch / 12
    val targetMonth = Month(totalMonthsSinceEpoch % 12 + 1)
    val clampedDay = minOf(anchorDay, daysInMonth(targetYear, targetMonth))
    return LocalDate(targetYear, targetMonth, clampedDay)
}

private fun daysInMonth(year: Int, month: Month): Int = when (month) {
    Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
    Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
    Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
    else -> 30
}

private fun isLeapYear(year: Int): Boolean = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0
