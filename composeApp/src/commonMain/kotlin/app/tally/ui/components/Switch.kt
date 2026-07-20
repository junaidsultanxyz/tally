package app.tally.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.Palette
import app.tally.ui.theme.TallyElevation
import app.tally.ui.theme.TallyShadowLevel
import app.tally.ui.theme.tallyShadow

/** Accessible toggle — `role = Switch` semantics via [Modifier.toggleable] (Switch.jsx). */
@Composable
internal fun TallySwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    description: String? = null,
    disabled: Boolean = false,
) {
    val colors = AppTheme.colors
    val trackShape = RoundedCornerShape(AppTheme.dimens.radii.pill)
    val knobOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = tween(durationMillis = AppTheme.motion.durationShort),
        label = "switch-knob",
    )

    Row(
        modifier = modifier
            .sizeIn(minHeight = AppTheme.dimens.tapMin)
            .alpha(if (disabled) 0.5f else 1f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (label != null || description != null) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                if (label != null) {
                    Text(
                        text = label,
                        color = colors.textPrimary,
                        fontSize = AppTheme.typography.sizes.body,
                        fontWeight = AppTheme.typography.weights.medium,
                        fontFamily = AppTheme.typography.fontFamily,
                    )
                }
                if (description != null) {
                    Text(
                        text = description,
                        color = colors.textTertiary,
                        fontSize = AppTheme.typography.sizes.caption,
                        fontFamily = AppTheme.typography.fontFamily,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .size(width = 52.dp, height = 32.dp)
                .clip(trackShape)
                .background(if (checked) colors.accent else colors.borderStrong, trackShape)
                .toggleable(
                    value = checked,
                    enabled = !disabled,
                    onValueChange = onCheckedChange,
                )
                .focusRing(cornerRadius = AppTheme.dimens.radii.pill)
                .padding(3.dp),
        ) {
            // Positioning (offset) and decoration (shadow/clip/background) are kept on
            // separate nested Boxes — chaining .offset() after .tallyShadow() on the same
            // modifier chain silently froze the knob in place (shadow()'s internal
            // graphicsLayer doesn't reliably re-place alongside a value-based offset).
            Box(modifier = Modifier.offset(x = knobOffset)) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .tallyShadow(TallyShadowLevel.SM, TallyElevation(isDark = colors.isDark), CircleShape)
                        .clip(CircleShape)
                        .background(Palette.neutral0, CircleShape),
                )
            }
        }
    }
}
