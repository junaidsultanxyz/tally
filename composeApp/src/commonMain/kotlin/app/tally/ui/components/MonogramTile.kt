package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Category
import app.tally.ui.theme.AppTheme

/**
 * Letter (subscription rows) or category icon (Add's category grid, Alerts
 * list) — category-tinted bg/fg, radius-md. The plan's default sizes are
 * 40dp/48dp; [SubscriptionRow] passes its own 44dp per SubscriptionRow.jsx.
 */
@Composable
internal fun TallyMonogramTile(
    category: Category,
    modifier: Modifier = Modifier,
    letter: String? = null,
    size: Dp = 44.dp,
) {
    val tint = categoryTint(category)
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)
    Box(
        modifier = modifier.size(size).clip(shape).background(tint.bg, shape),
        contentAlignment = Alignment.Center,
    ) {
        if (letter != null) {
            Text(
                text = letter,
                color = tint.fg,
                fontWeight = AppTheme.typography.weights.bold,
                fontSize = AppTheme.typography.sizes.bodyLg,
                fontFamily = AppTheme.typography.fontFamily,
            )
        } else {
            Icon(
                imageVector = categoryIcon(category),
                contentDescription = categoryLabel(category),
                tint = tint.fg,
                modifier = Modifier.size(size * 0.5f),
            )
        }
    }
}
