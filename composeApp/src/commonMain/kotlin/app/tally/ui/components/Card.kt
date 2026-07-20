package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.TallyElevation
import app.tally.ui.theme.TallyShadowLevel
import app.tally.ui.theme.tallyShadow

/** Default (hairline border + `shadow-xs`) or floating (`shadow-md`) — radius-xl either way (Card.jsx). */
@Composable
internal fun TallyCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(AppTheme.dimens.spacing.space5),
    elevated: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.dimens.radii.xl)
    val interactionSource = remember { MutableInteractionSource() }

    var cardModifier = modifier
        .tallyShadow(
            level = if (elevated) TallyShadowLevel.MD else TallyShadowLevel.XS,
            elevation = TallyElevation(isDark = colors.isDark),
            shape = shape,
        )
        .clip(shape)
        .background(colors.bgSurface, shape)
        .border(1.dp, colors.borderSubtle, shape)

    if (onClick != null) {
        cardModifier = cardModifier
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusRing(cornerRadius = AppTheme.dimens.radii.xl)
    }
    cardModifier = cardModifier.padding(padding)

    Column(modifier = cardModifier) {
        content()
    }
}
