package app.tally.domain.usecase

import app.tally.domain.model.Status
import kotlinx.datetime.LocalDate

/**
 * The *kind* of due-text to show — not a localized string. Resolving this to
 * actual display text (with plurals) happens in the UI layer via string
 * resources; keeping that split out of this use-case keeps it Compose-free
 * and trivially unit testable (AGENTS.md §18 — no business logic in composables,
 * and conversely, no UI-framework dependency in use-cases).
 */
public sealed interface DueTextKind {
    public data object Paused : DueTextKind
    public data class TrialEndsIn(val days: Int) : DueTextKind
    public data object Today : DueTextKind
    public data object Tomorrow : DueTextKind
    public data class InDays(val days: Int) : DueTextKind
    public data class OverdueBy(val days: Int) : DueTextKind
}

/**
 * [nextBillingDate] + [status] + [today] → a [DueTextKind]. Mirrors the
 * kit's due-text rules (FUNCTIONALITIES.md) exactly, just derived from a
 * real date instead of parsed from free text (AGENTS.md §8). Trial status
 * always renders as "trial ends in Nd" — it is checked before the
 * today/tomorrow special cases, matching the kit (a trial ending today still
 * reads "trial ends in 0d", not "trial ends today").
 */
public fun deriveDueTextKind(nextBillingDate: LocalDate, status: Status, today: LocalDate): DueTextKind {
    if (status == Status.PAUSED) return DueTextKind.Paused

    val days = daysUntil(nextBillingDate, status, today)
    return when {
        status == Status.TRIAL -> DueTextKind.TrialEndsIn(days)
        days == 0 -> DueTextKind.Today
        days == 1 -> DueTextKind.Tomorrow
        days > 1 -> DueTextKind.InDays(days)
        else -> DueTextKind.OverdueBy(-days)
    }
}
