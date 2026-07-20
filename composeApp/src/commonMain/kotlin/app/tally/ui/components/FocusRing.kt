package app.tally.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import app.tally.ui.theme.AppTheme

/**
 * The always-visible keyboard focus ring — mirrors the kit's
 * `outline: var(--focus-ring-width) solid var(--focus-ring); outline-offset: var(--focus-ring-offset)`.
 * Every interactive component applies this. Chain it **after** `.clickable()`
 * (or another modifier that calls `.focusable()`) — this modifier only draws
 * the ring in response to focus, it doesn't make the element focusable itself.
 */
@Composable
internal fun Modifier.focusRing(cornerRadius: Dp = AppTheme.dimens.radii.md): Modifier {
    var isFocused by remember { mutableStateOf(false) }
    val ringColor = AppTheme.colors.focusRing
    val ringWidth = AppTheme.dimens.focusRingWidth
    val ringOffset = AppTheme.dimens.focusRingOffset

    return this
        .onFocusEvent { isFocused = it.isFocused }
        .drawWithContent {
            drawContent()
            if (isFocused) {
                val strokePx = ringWidth.toPx()
                val inflate = ringOffset.toPx() + strokePx / 2
                drawRoundRect(
                    color = ringColor,
                    topLeft = Offset(-inflate, -inflate),
                    size = Size(size.width + inflate * 2, size.height + inflate * 2),
                    cornerRadius = CornerRadius(cornerRadius.toPx() + inflate),
                    style = Stroke(width = strokePx),
                )
            }
        }
}
