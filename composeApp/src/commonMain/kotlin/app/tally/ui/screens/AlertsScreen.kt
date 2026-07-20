package app.tally.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.daysUntil
import app.tally.money.formatMoney
import app.tally.platform.formatAbsoluteDate
import app.tally.ui.components.TallyListCard
import app.tally.ui.components.TallySwitch
import app.tally.ui.components.categoryIcon
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.alerts_empty
import tally.composeapp.generated.resources.alerts_reminder_switch_label
import tally.composeapp.generated.resources.alerts_row_subline
import tally.composeapp.generated.resources.alerts_subtitle
import tally.composeapp.generated.resources.alerts_then_amount
import tally.composeapp.generated.resources.alerts_title
import tally.composeapp.generated.resources.reminder_footer_lead_days
import tally.composeapp.generated.resources.renews_in_days
import tally.composeapp.generated.resources.trial_ends_in_days

@Composable
internal fun AlertsScreen(viewModel: AlertsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.dimens.gutter),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.alerts_title),
                    color = colors.textTertiary,
                    fontSize = typography.sizes.callout,
                    fontFamily = typography.fontFamily,
                )
                Text(
                    text = stringResource(Res.string.alerts_subtitle),
                    color = colors.textPrimary,
                    fontSize = typography.sizes.title,
                    fontWeight = typography.weights.bold,
                    fontFamily = typography.fontFamily,
                )
            }
        }

        when (val state = uiState) {
            AlertsUiState.Loading -> Unit
            AlertsUiState.Empty -> {
                item {
                    Text(
                        text = stringResource(Res.string.alerts_empty),
                        color = colors.textTertiary,
                        fontSize = typography.sizes.body,
                        fontFamily = typography.fontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    )
                }
            }
            is AlertsUiState.Content -> {
                item {
                    TallyListCard(
                        rows = state.reminders.map { subscription ->
                            { AlertsRowItem(subscription, viewModel) }
                        },
                    )
                }
                item {
                    Text(
                        text = pluralStringResource(Res.plurals.reminder_footer_lead_days, state.leadDays, state.leadDays),
                        color = colors.textTertiary,
                        fontSize = typography.sizes.caption,
                        fontFamily = typography.fontFamily,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertsRowItem(subscription: Subscription, viewModel: AlertsViewModel) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val days = daysUntil(subscription.nextBillingDate, subscription.status, today)
    val isTrial = subscription.status == Status.TRIAL

    val renewsText = if (isTrial) {
        pluralStringResource(Res.plurals.trial_ends_in_days, days, days)
    } else {
        pluralStringResource(Res.plurals.renews_in_days, days, days)
    }
    val dateText = formatAbsoluteDate(subscription.nextBillingDate)
    val amountText = formatMoney(subscription.amountMinor, subscription.currencyCode)
    val amountWithPrefix = if (isTrial) stringResource(Res.string.alerts_then_amount, amountText) else amountText
    val subline = stringResource(Res.string.alerts_row_subline, renewsText, dateText, amountWithPrefix)
    val switchLabel = stringResource(Res.string.alerts_reminder_switch_label, subscription.name)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(AppTheme.dimens.radii.md))
                .background(colors.accentSoft, RoundedCornerShape(AppTheme.dimens.radii.md)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = categoryIcon(subscription.category),
                contentDescription = null,
                tint = colors.accentSoftFg,
                modifier = Modifier.size(20.dp),
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = subscription.name,
                color = colors.textPrimary,
                fontSize = typography.sizes.body,
                fontWeight = typography.weights.medium,
                fontFamily = typography.fontFamily,
            )
            Text(
                text = subline,
                color = colors.textTertiary,
                fontSize = typography.sizes.caption,
                fontFamily = typography.fontFamily,
            )
        }
        // Reflects the subscription's real reminderEnabled state — the row itself always
        // stays in the list regardless (AlertsViewModel), only the switch position changes.
        TallySwitch(
            checked = subscription.reminderEnabled,
            onCheckedChange = { enabled -> viewModel.setReminderEnabled(subscription.id, enabled) },
            modifier = Modifier.semantics { contentDescription = switchLabel },
        )
    }
}
