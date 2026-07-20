package app.tally.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Subscription
import app.tally.money.formatMoney
import app.tally.ui.components.TallyListCard
import app.tally.ui.components.TallySegmentedControl
import app.tally.ui.components.TallySubscriptionRow
import app.tally.ui.components.statusRowLabel
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.upcoming_bucket_later_this_month
import tally.composeapp.generated.resources.upcoming_bucket_next_week
import tally.composeapp.generated.resources.upcoming_bucket_this_week
import tally.composeapp.generated.resources.upcoming_empty
import tally.composeapp.generated.resources.upcoming_filter_all
import tally.composeapp.generated.resources.upcoming_filter_month
import tally.composeapp.generated.resources.upcoming_filter_week
import tally.composeapp.generated.resources.upcoming_subtitle
import tally.composeapp.generated.resources.upcoming_title

@Composable
internal fun UpcomingScreen(
    onOpenSubscription: (String) -> Unit = {},
    viewModel: UpcomingViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    // Resolved up front, in a real composable context — LazyColumn's content lambda is a
    // scope-builder, not a composable context itself outside of item {} blocks, so
    // stringResource() can't be called directly inside the `else` branch below.
    val filterLabels = mapOf(
        UpcomingFilter.WEEK to stringResource(Res.string.upcoming_filter_week),
        UpcomingFilter.MONTH to stringResource(Res.string.upcoming_filter_month),
        UpcomingFilter.ALL to stringResource(Res.string.upcoming_filter_all),
    )
    val thisWeekLabel = stringResource(Res.string.upcoming_bucket_this_week)
    val nextWeekLabel = stringResource(Res.string.upcoming_bucket_next_week)
    val laterThisMonthLabel = stringResource(Res.string.upcoming_bucket_later_this_month)
    val emptyText = stringResource(Res.string.upcoming_empty)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.dimens.gutter),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.upcoming_title),
                    color = colors.textPrimary,
                    fontSize = typography.sizes.title,
                    fontWeight = typography.weights.bold,
                    fontFamily = typography.fontFamily,
                )
                Text(
                    text = stringResource(Res.string.upcoming_subtitle),
                    color = colors.textTertiary,
                    fontSize = typography.sizes.callout,
                    fontFamily = typography.fontFamily,
                )
            }
        }

        when (val state = uiState) {
            UpcomingUiState.Loading -> Unit
            is UpcomingUiState.Content -> {
                item {
                    TallySegmentedControl(
                        options = UpcomingFilter.entries.toList(),
                        selected = state.filter,
                        onSelect = viewModel::setFilter,
                        labelOf = { filterLabels.getValue(it) },
                    )
                }

                if (state.isEmpty) {
                    item {
                        Text(
                            text = emptyText,
                            color = colors.textTertiary,
                            fontSize = typography.sizes.body,
                            fontFamily = typography.fontFamily,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        )
                    }
                } else {
                    bucket(thisWeekLabel, state.thisWeek, state.activeCurrency, onOpenSubscription, viewModel)
                    bucket(nextWeekLabel, state.nextWeek, state.activeCurrency, onOpenSubscription, viewModel)
                    bucket(laterThisMonthLabel, state.laterThisMonth, state.activeCurrency, onOpenSubscription, viewModel)
                }
            }
        }
    }
}

// Buckets with no members are omitted entirely (matches the kit).
private fun LazyListScope.bucket(
    label: String,
    subscriptions: List<Subscription>,
    currency: String,
    onOpenSubscription: (String) -> Unit,
    viewModel: UpcomingViewModel,
) {
    if (subscriptions.isEmpty()) return
    item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BucketHeader(label)
            TallyListCard(
                rows = subscriptions.map { sub ->
                    { UpcomingRowItem(sub, currency, onOpenSubscription, viewModel) }
                },
            )
        }
    }
}

@Composable
private fun BucketHeader(label: String) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Text(
        text = label.uppercase(),
        color = colors.textSecondary,
        fontSize = typography.sizes.caption,
        fontWeight = typography.weights.semibold,
        fontFamily = typography.fontFamily,
        letterSpacing = typography.tracking.wide,
    )
}

@Composable
private fun UpcomingRowItem(
    subscription: Subscription,
    currency: String,
    onOpenSubscription: (String) -> Unit,
    viewModel: UpcomingViewModel,
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    TallySubscriptionRow(
        name = subscription.name,
        amount = formatMoney(subscription.amountMinor, subscription.currencyCode),
        category = subscription.category,
        cadence = if (subscription.billingCycle == app.tally.domain.model.BillingCycle.YEARLY) "yr" else "mo",
        status = subscription.status,
        statusLabel = statusRowLabel(subscription, today),
        onClick = { onOpenSubscription(subscription.id) },
        onDelete = { viewModel.deleteSubscription(subscription.id) },
    )
}
