package app.tally.domain.model

import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/**
 * The core domain model — exactly the shape in AGENTS.md §8. Real dates, not
 * free-text parsing (that's the kit's approach, deliberately not replicated
 * here — see `DeriveDueText`/`DaysUntil` for how display strings/day-counts
 * are derived from [nextBillingDate] instead of stored).
 */
public data class Subscription(
    val id: String, // UUID
    val name: String,
    val category: Category,
    val amountMinor: Long,
    val currencyCode: String,
    val billingCycle: BillingCycle,
    val firstBillingDate: LocalDate,
    val nextBillingDate: LocalDate,
    val status: Status,
    val reminderEnabled: Boolean = true, // "remind before renewal" default ON (AGENTS.md §11)
    val reminderLeadDays: Int = 3,
    val reminderTime: LocalTime,
    val updatedAt: Instant, // sync conflict resolution (last-write-wins)
    val deletedAt: Instant? = null, // tombstone for sync
)
