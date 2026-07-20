package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.TallyElevation
import app.tally.ui.theme.TallyShadowLevel
import app.tally.ui.theme.tallyShadow

/**
 * Single-select pill container (SegmentedControl.jsx). Used by the Upcoming
 * filter, Add cadence, and Settings text-size controls (Phase 2).
 */
@Composable
internal fun <T> TallySegmentedControl(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    labelOf: (T) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = AppTheme.colors
    val outerShape = RoundedCornerShape(AppTheme.dimens.radii.md)
    val segmentShape = RoundedCornerShape(AppTheme.dimens.radii.md - 3.dp)

    // No "tablist" Role exists in Compose's built-in Role enum; each segment's
    // Role.Tab below is enough for accessibility services to convey the group.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerShape)
            .background(colors.bgSunken, outerShape)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        options.forEach { option ->
            val isActive = option == selected
            val interactionSource = remember { MutableInteractionSource() }
            var segmentModifier = Modifier
                .weight(1f) // RowScope.weight — resolvable here since this forEach body is inside the Row {} content lambda.
                .heightIn(min = 38.dp)
                .clip(segmentShape)
            if (isActive) {
                segmentModifier = segmentModifier
                    .tallyShadow(TallyShadowLevel.XS, TallyElevation(isDark = colors.isDark), segmentShape)
                    .background(colors.bgSurface, segmentShape)
            }
            segmentModifier = segmentModifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onSelect(option) },
                )
                .focusRing(cornerRadius = AppTheme.dimens.radii.md - 3.dp)
                .padding(horizontal = 14.dp)
                .semantics { this.role = Role.Tab; this.selected = isActive }

            Box(modifier = segmentModifier, contentAlignment = Alignment.Center) {
                Text(
                    text = labelOf(option),
                    color = if (isActive) colors.textPrimary else colors.textSecondary,
                    fontSize = AppTheme.typography.sizes.callout,
                    fontWeight = AppTheme.typography.weights.semibold,
                    fontFamily = AppTheme.typography.fontFamily,
                )
            }
        }
    }
}
