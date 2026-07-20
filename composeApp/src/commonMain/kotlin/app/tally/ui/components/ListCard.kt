package app.tally.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme

/**
 * The grouped-rows container used by every list section (Dashboard,
 * Upcoming, Alerts) — a [TallyCard] whose vertical padding is thin (6dp)
 * since each row (e.g. [TallySubscriptionRow]) already carries its own
 * 12dp vertical padding (subscriptions.card.html). A hairline divider
 * separates rows — never rendered after the last one.
 */
@Composable
internal fun TallyListCard(rows: List<@Composable () -> Unit>, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    TallyCard(
        modifier = modifier,
        padding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
    ) {
        rows.forEachIndexed { index, row ->
            row()
            if (index != rows.lastIndex) {
                HorizontalDivider(color = colors.borderSubtle, thickness = 1.dp)
            }
        }
    }
}
