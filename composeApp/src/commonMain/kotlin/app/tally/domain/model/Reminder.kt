package app.tally.domain.model

import kotlin.time.Instant

/** The scheduled-notification record (AGENTS.md §13) — computed from a [Subscription], not stored redundantly. */
public data class Reminder(
    val id: String,
    val subscriptionId: String,
    val fireAt: Instant,
    val enabled: Boolean,
)
