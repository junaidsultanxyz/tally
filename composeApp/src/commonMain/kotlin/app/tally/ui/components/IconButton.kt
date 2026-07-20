package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import app.tally.ui.theme.AppTheme

internal enum class IconButtonVariant { GHOST, SURFACE, SOFT }

/** Min size is always `tapMin` — [size] can only make it bigger, never smaller (IconButton.jsx). */
@Composable
internal fun TallyIconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: IconButtonVariant = IconButtonVariant.GHOST,
    size: Dp = AppTheme.dimens.tapMin,
    disabled: Boolean = false,
) {
    val colors = AppTheme.colors
    val background = when (variant) {
        IconButtonVariant.GHOST -> Color.Transparent
        IconButtonVariant.SURFACE -> colors.bgSurface
        IconButtonVariant.SOFT -> colors.accentSoft
    }
    val content = if (variant == IconButtonVariant.SOFT) colors.accentSoftFg else colors.textSecondary
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)
    val resolvedSize = max(size, AppTheme.dimens.tapMin)
    val interactionSource = remember { MutableInteractionSource() }

    var boxModifier = modifier
        .size(resolvedSize)
        .clip(shape)
        .background(background, shape)
    if (variant == IconButtonVariant.SURFACE) {
        boxModifier = boxModifier.border(1.5.dp, colors.borderDefault, shape)
    }
    boxModifier = boxModifier
        .clickable(
            enabled = !disabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
        .focusRing(cornerRadius = AppTheme.dimens.radii.md)

    Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = content.copy(alpha = if (disabled) 0.5f else 1f),
        )
    }
}
