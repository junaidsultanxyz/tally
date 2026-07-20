package app.tally.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** `--space-*` tokens from `tokens/spacing.css` (4px base scale). */
internal data class TallySpacing(
    val space0: Dp = 0.dp,
    val space1: Dp = 4.dp,
    val space2: Dp = 8.dp,
    val space3: Dp = 12.dp,
    val space4: Dp = 16.dp,
    val space5: Dp = 20.dp,
    val space6: Dp = 24.dp,
    val space8: Dp = 32.dp,
    val space10: Dp = 40.dp,
    val space12: Dp = 48.dp,
    val space16: Dp = 64.dp,
)

/** `--radius-*` tokens from `tokens/radii.css`. */
internal data class TallyRadii(
    val xs: Dp = 6.dp,
    val sm: Dp = 10.dp,
    val md: Dp = 14.dp,
    val lg: Dp = 18.dp,
    val xl: Dp = 24.dp,
    val xxl: Dp = 32.dp,
    val pill: Dp = 999.dp,
)

/**
 * Layout tokens whose value depends on accessibility settings: `--gutter` /
 * `--tap-min` widen under `[data-tap="large"]`, `--focus-ring-width` widens
 * under `[data-contrast="high"]` (`tokens/spacing.css`, `accessibility.css`).
 */
internal data class TallyDimens(
    val spacing: TallySpacing,
    val radii: TallyRadii,
    val gutter: Dp,
    val tapMin: Dp,
    val focusRingWidth: Dp,
    val focusRingOffset: Dp,
)

internal fun tallyDimens(largeTap: Boolean, highContrast: Boolean): TallyDimens = TallyDimens(
    spacing = TallySpacing(),
    radii = TallyRadii(),
    gutter = if (largeTap) 24.dp else 20.dp,
    tapMin = if (largeTap) 52.dp else 44.dp,
    focusRingWidth = if (highContrast) 4.dp else 3.dp,
    focusRingOffset = 2.dp,
)
