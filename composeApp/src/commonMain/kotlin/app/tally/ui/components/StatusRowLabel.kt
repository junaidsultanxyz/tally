package app.tally.ui.components

import androidx.compose.runtime.Composable
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.DueTextKind
import app.tally.domain.usecase.deriveDueTextKind
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.status_active
import tally.composeapp.generated.resources.status_badge_due_days
import tally.composeapp.generated.resources.status_badge_due_today
import tally.composeapp.generated.resources.status_badge_due_tomorrow
import tally.composeapp.generated.resources.status_badge_overdue_days
import tally.composeapp.generated.resources.status_badge_trial_days
import tally.composeapp.generated.resources.status_paused

/**
 * The status badge's label text, folding in due-timing since rows have no
 * separate subline for it (matches the kit's own "● Due 3d" pill —
 * subscriptions.card.html).
 */
@Composable
public fun statusRowLabel(subscription: Subscription, today: LocalDate): String {
    val kind = deriveDueTextKind(subscription.nextBillingDate, subscription.status, today)
    return when (subscription.status) {
        Status.PAUSED -> stringResource(Res.string.status_paused)
        Status.ACTIVE -> stringResource(Res.string.status_active)
        Status.TRIAL -> when (kind) {
            is DueTextKind.TrialEndsIn -> stringResource(Res.string.status_badge_trial_days, kind.days)
            else -> stringResource(Res.string.status_active)
        }
        Status.DUE, Status.OVERDUE -> when (kind) {
            DueTextKind.Today -> stringResource(Res.string.status_badge_due_today)
            DueTextKind.Tomorrow -> stringResource(Res.string.status_badge_due_tomorrow)
            is DueTextKind.InDays -> stringResource(Res.string.status_badge_due_days, kind.days)
            is DueTextKind.OverdueBy -> stringResource(Res.string.status_badge_overdue_days, kind.days)
            else -> stringResource(Res.string.status_active)
        }
    }
}
