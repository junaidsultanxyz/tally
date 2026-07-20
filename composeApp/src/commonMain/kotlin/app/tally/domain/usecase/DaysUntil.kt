package app.tally.domain.usecase

import app.tally.domain.model.Status
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

/**
 * Integer days from [today] to [nextBillingDate]. Paused subscriptions
 * return the sentinel `99` (AGENTS.md §8) to match the kit's filter
 * behavior — callers must not treat 99 as a real day count. Compute in the
 * device's local date, never UTC — [today] should come from the platform's
 * local `Clock`/`TimeZone`, not `Clock.System.now()` in UTC.
 */
public fun daysUntil(nextBillingDate: LocalDate, status: Status, today: LocalDate): Int {
    if (status == Status.PAUSED) return 99
    return today.daysUntil(nextBillingDate)
}
