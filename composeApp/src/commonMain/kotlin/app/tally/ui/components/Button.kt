package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme

internal enum class ButtonVariant { PRIMARY, SECONDARY, GHOST, DANGER }
internal enum class ButtonSize { SM, MD, LG }

private data class ButtonColors(val background: Color, val content: Color, val border: Color?)

/** Pill-shaped, min height [ButtonSize]-dependent (`tapMin` for MD, matching Button.jsx). */
@Composable
internal fun TallyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    size: ButtonSize = ButtonSize.MD,
    fullWidth: Boolean = false,
    disabled: Boolean = false,
    iconLeft: ImageVector? = null,
    iconRight: ImageVector? = null,
) {
    val colors = AppTheme.colors
    val buttonColors = when (variant) {
        ButtonVariant.PRIMARY -> ButtonColors(colors.accent, colors.textOnAccent, null)
        ButtonVariant.SECONDARY -> ButtonColors(colors.bgSurface, colors.textPrimary, colors.borderDefault)
        ButtonVariant.GHOST -> ButtonColors(Color.Transparent, colors.accent, null)
        ButtonVariant.DANGER -> ButtonColors(colors.dangerBg, colors.dangerFg, null)
    }
    val (fontSize, horizontalPadding, minHeight) = when (size) {
        ButtonSize.SM -> Triple(AppTheme.typography.sizes.callout, 14.dp, 36.dp)
        ButtonSize.MD -> Triple(AppTheme.typography.sizes.body, 20.dp, AppTheme.dimens.tapMin)
        ButtonSize.LG -> Triple(AppTheme.typography.sizes.bodyLg, 26.dp, 56.dp)
    }
    val shape = RoundedCornerShape(AppTheme.dimens.radii.pill)
    val interactionSource = remember { MutableInteractionSource() }

    var rowModifier = modifier
        .let { if (fullWidth) it.fillMaxWidth() else it.wrapContentWidth() }
        .heightIn(min = minHeight)
        .clip(shape)
        .background(buttonColors.background, shape)
    if (buttonColors.border != null) {
        rowModifier = rowModifier.border(1.5.dp, buttonColors.border, shape)
    }
    rowModifier = rowModifier
        .clickable(
            enabled = !disabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
        .focusRing(cornerRadius = AppTheme.dimens.radii.pill)
        .padding(PaddingValues(horizontal = horizontalPadding))

    CompositionLocalProvider(LocalContentColor provides buttonColors.content.copy(alpha = if (disabled) 0.5f else 1f)) {
        Row(
            modifier = rowModifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            iconLeft?.let { Icon(it, contentDescription = null, tint = LocalContentColor.current) }
            Text(
                text = text,
                color = LocalContentColor.current,
                fontSize = fontSize,
                fontWeight = AppTheme.typography.weights.semibold,
                fontFamily = AppTheme.typography.fontFamily,
            )
            iconRight?.let { Icon(it, contentDescription = null, tint = LocalContentColor.current) }
        }
    }
}
