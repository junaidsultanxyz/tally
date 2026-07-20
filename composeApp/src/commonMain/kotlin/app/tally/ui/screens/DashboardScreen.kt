package app.tally.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Subscription
import app.tally.domain.usecase.MonthlyDeltaDirection
import app.tally.money.formatMoney
import app.tally.ui.components.TallyIconButton
import app.tally.ui.components.TallyIcons
import app.tally.ui.components.TallyListCard
import app.tally.ui.components.TallySegmentedControl
import app.tally.ui.components.TallySubscriptionRow
import app.tally.ui.components.TallySummaryStat
import app.tally.ui.components.IconButtonVariant
import app.tally.ui.components.SummaryStatTone
import app.tally.ui.components.statusRowLabel
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.active_subscriptions_count
import tally.composeapp.generated.resources.dashboard_all_subscriptions_header
import tally.composeapp.generated.resources.dashboard_cause_price_drop
import tally.composeapp.generated.resources.dashboard_cause_price_rise
import tally.composeapp.generated.resources.dashboard_empty_no_subscriptions
import tally.composeapp.generated.resources.dashboard_empty_renewing_soon
import tally.composeapp.generated.resources.dashboard_hero_annual_suffix
import tally.composeapp.generated.resources.dashboard_hero_label
import tally.composeapp.generated.resources.dashboard_notifications
import tally.composeapp.generated.resources.dashboard_renewing_soon_header
import tally.composeapp.generated.resources.dashboard_see_all
import tally.composeapp.generated.resources.dashboard_sort
import tally.composeapp.generated.resources.dashboard_sort_name_a_z
import tally.composeapp.generated.resources.dashboard_sort_next_charge
import tally.composeapp.generated.resources.dashboard_sort_price_high_low
import tally.composeapp.generated.resources.dashboard_stat_upcoming_label
import tally.composeapp.generated.resources.dashboard_stat_vs_last_month_label
import tally.composeapp.generated.resources.dashboard_title

@Composable
internal fun DashboardScreen(
    onNavigateToAlerts: () -> Unit,
    onOpenSubscription: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.dimens.gutter),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.dashboard_title),
                    color = colors.textPrimary,
                    fontSize = typography.sizes.title,
                    fontWeight = typography.weights.bold,
                    fontFamily = typography.fontFamily,
                )
                TallyIconButton(
                    icon = TallyIcons.bell,
                    label = stringResource(Res.string.dashboard_notifications),
                    onClick = onNavigateToAlerts,
                    variant = IconButtonVariant.SOFT,
                )
            }
        }

        when (val state = uiState) {
            DashboardUiState.Loading -> Unit
            DashboardUiState.Empty -> item {
                EmptyStateText(stringResource(Res.string.dashboard_empty_no_subscriptions))
            }
            is DashboardUiState.Content -> {
                item { HeroCard(state) }
                item { StatCardsRow(state) }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        SectionHeader(stringResource(Res.string.dashboard_renewing_soon_header))
                        if (state.renewingSoon.isEmpty()) {
                            EmptyStateText(stringResource(Res.string.dashboard_empty_renewing_soon))
                        } else {
                            TallyListCard(
                                rows = state.renewingSoon.map { sub ->
                                    { SubscriptionRowItem(sub, showDelete = false, onOpenSubscription, viewModel) }
                                },
                            )
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            SectionHeader(stringResource(Res.string.dashboard_all_subscriptions_header))
                            Text(
                                text = stringResource(Res.string.dashboard_see_all),
                                color = colors.accent,
                                fontSize = typography.sizes.callout,
                                fontWeight = typography.weights.semibold,
                                fontFamily = typography.fontFamily,
                            )
                        }
                        SortControl(state.sortOrder, viewModel::setSortOrder)
                        TallyListCard(
                            rows = state.allSubscriptions.map { sub ->
                                { SubscriptionRowItem(sub, showDelete = true, onOpenSubscription, viewModel) }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionRowItem(
    subscription: Subscription,
    showDelete: Boolean,
    onOpenSubscription: (String) -> Unit,
    viewModel: DashboardViewModel,
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
        onDelete = if (showDelete) ({ viewModel.deleteSubscription(subscription.id) }) else null,
    )
}

@Composable
private fun HeroCard(state: DashboardUiState.Content) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(AppTheme.dimens.radii.xxl)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.accent, shape)
            .padding(AppTheme.dimens.spacing.space5),
    ) {
        // matchParentSize() (not just an offset on a plain Box) is what keeps these purely
        // decorative — a plain Box's own measured size+offset would otherwise unioned into
        // this Box's own size, since Modifier.offset() only repositions post-layout, it
        // doesn't exempt a child from contributing to the parent's size. That inflated the
        // card well past the text content's real height, reading as lopsided bottom padding.
        Box(modifier = Modifier.matchParentSize().clip(shape)) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 220.dp, y = (-60).dp)
                    .clip(CircleShape)
                    .background(colors.textOnAccent.copy(alpha = 0.08f)),
            )
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 260.dp, y = 90.dp)
                    .clip(CircleShape)
                    .background(colors.textOnAccent.copy(alpha = 0.08f)),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(Res.string.dashboard_hero_label),
                color = colors.textOnAccent.copy(alpha = 0.85f),
                fontSize = typography.sizes.callout,
                fontWeight = typography.weights.semibold,
                fontFamily = typography.fontFamily,
            )
            Text(
                text = formatMoney(state.monthlyTotalMinor, state.activeCurrency),
                style = typography.style(size = typography.sizes.display, weight = typography.weights.bold)
                    .copy(color = colors.textOnAccent),
            )
            Text(
                text = pluralStringResource(Res.plurals.active_subscriptions_count, state.activeCount, state.activeCount) +
                    " · " + stringResource(Res.string.dashboard_hero_annual_suffix, formatMoney(state.annualTotalMinor, state.activeCurrency)),
                color = colors.textOnAccent.copy(alpha = 0.85f),
                fontSize = typography.sizes.caption,
                fontFamily = typography.fontFamily,
            )
        }
    }
}

@Composable
private fun StatCardsRow(state: DashboardUiState.Content) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        // Commented out for now, at the user's explicit request — coming back once "vs last
        // month" is implemented alongside it, not deleted.
        // Box(modifier = Modifier.weight(1f)) {
        //     TallySummaryStat(
        //         label = stringResource(Res.string.dashboard_stat_upcoming_label),
        //         value = state.upcomingCount.toString(),
        //     )
        // }
        val delta = state.monthlyDelta
        if (delta != null) {
            Box(modifier = Modifier.weight(1f)) {
                val sign = if (delta.deltaMinor >= 0) "+" else ""
                val causeText = delta.cause?.let { cause ->
                    when (cause.direction) {
                        MonthlyDeltaDirection.PriceRise -> stringResource(Res.string.dashboard_cause_price_rise, cause.subscriptionName)
                        MonthlyDeltaDirection.PriceDrop -> stringResource(Res.string.dashboard_cause_price_drop, cause.subscriptionName)
                    }
                }
                TallySummaryStat(
                    label = stringResource(Res.string.dashboard_stat_vs_last_month_label),
                    value = sign + formatMoney(delta.deltaMinor, state.activeCurrency),
                    sublabel = causeText,
                    tone = if (delta.deltaMinor > 0) SummaryStatTone.WARNING else SummaryStatTone.DEFAULT,
                )
            }
        }
    }
}

@Composable
private fun SortControl(current: DashboardSortOrder, onChange: (DashboardSortOrder) -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    // TallySegmentedControl's labelOf is a plain (T) -> String, not @Composable — resolve
    // the strings here, in a real composable context, then just look them up below.
    val labels = mapOf(
        DashboardSortOrder.NEXT_CHARGE to stringResource(Res.string.dashboard_sort_next_charge),
        DashboardSortOrder.PRICE_HIGH_TO_LOW to stringResource(Res.string.dashboard_sort_price_high_low),
        DashboardSortOrder.NAME_A_TO_Z to stringResource(Res.string.dashboard_sort_name_a_z),
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.dashboard_sort),
            color = colors.textSecondary,
            fontSize = typography.sizes.caption,
            fontFamily = typography.fontFamily,
        )
        TallySegmentedControl(
            options = DashboardSortOrder.entries.toList(),
            selected = current,
            onSelect = onChange,
            labelOf = { order -> labels.getValue(order) },
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Text(
        text = text,
        color = colors.textSecondary,
        fontSize = typography.sizes.callout,
        fontWeight = typography.weights.semibold,
        fontFamily = typography.fontFamily,
    )
}

@Composable
private fun EmptyStateText(text: String) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Text(
        text = text,
        color = colors.textTertiary,
        fontSize = typography.sizes.body,
        fontFamily = typography.fontFamily,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
    )
}
