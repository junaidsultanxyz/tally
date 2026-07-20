package app.tally.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import app.tally.data.settings.AppearanceSettings

internal val LocalTallyColors = staticCompositionLocalOf<TallyColors> {
    error("No TallyColors provided — wrap content in AppTheme")
}
internal val LocalTallyTypography = staticCompositionLocalOf<TallyTypography> {
    error("No TallyTypography provided — wrap content in AppTheme")
}
internal val LocalTallyDimens = staticCompositionLocalOf<TallyDimens> {
    error("No TallyDimens provided — wrap content in AppTheme")
}
internal val LocalTallyElevation = staticCompositionLocalOf<TallyElevation> {
    error("No TallyElevation provided — wrap content in AppTheme")
}

/**
 * The native equivalent of setting `data-theme` / `data-contrast` /
 * `data-text-size` / `data-tap` / `data-motion` on the kit's app root — every
 * combination of [settings] must compose freely (dark + high-contrast +
 * xx-large + large-tap + reduced-motion all together is a valid, supported
 * state). Read tokens via [AppTheme]'s accessors, never [Palette] or a
 * hardcoded literal, from anywhere under [content].
 */
@Composable
internal fun AppTheme(settings: AppearanceSettings, content: @Composable () -> Unit) {
    val isDark = settings.darkMode ?: isSystemInDarkTheme()

    val colors = when {
        isDark && settings.highContrast -> DarkHighContrastTallyColors
        isDark -> DarkTallyColors
        settings.highContrast -> LightHighContrastTallyColors
        else -> LightTallyColors
    }

    val typography = rememberTallyTypography(scale = settings.textSize.scale, boldText = settings.boldText)
    val dimens = tallyDimens(largeTap = settings.largeTap, highContrast = settings.highContrast)
    val elevation = TallyElevation(isDark = isDark)

    val systemReducedMotion = isSystemReducedMotionEnabled()
    val motion = TallyMotion(enabled = !(settings.reducedMotion || systemReducedMotion))

    CompositionLocalProvider(
        LocalTallyColors provides colors,
        LocalTallyTypography provides typography,
        LocalTallyDimens provides dimens,
        LocalTallyElevation provides elevation,
        LocalMotion provides motion,
    ) {
        MaterialTheme(colorScheme = tallyColorScheme(colors, isDark), content = content)
    }
}

/**
 * Maps [TallyColors] onto a Material3 [ColorScheme] so components that fall
 * back to Material3 defaults (`Scaffold`'s `containerColor`, ripple/indication,
 * text-selection handles, ...) stay in sync with our own dark/light/high-contrast
 * decision instead of silently rendering Material's own baseline scheme —
 * this is exactly what happened before this mapping existed: the bottom bar
 * (which reads [TallyColors] directly) went dark correctly while `Scaffold`'s
 * content area (relying on the Material3 default) stayed on Material's
 * baseline light lavender regardless of our theme. Unmapped slots fall back
 * to Material3's own dark/light baseline, matched to [isDark].
 */
private fun tallyColorScheme(colors: TallyColors, isDark: Boolean): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = colors.accent,
        onPrimary = colors.textOnAccent,
        primaryContainer = colors.accentSoft,
        onPrimaryContainer = colors.accentSoftFg,
        secondary = colors.accent,
        onSecondary = colors.textOnAccent,
        background = colors.bgApp,
        onBackground = colors.textPrimary,
        surface = colors.bgSurface,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.bgSunken,
        onSurfaceVariant = colors.textSecondary,
        error = colors.dangerFg,
        onError = colors.textOnAccent,
        errorContainer = colors.dangerBg,
        onErrorContainer = colors.dangerFg,
        outline = colors.borderDefault,
        outlineVariant = colors.borderSubtle,
    )
}

/** `AppTheme.colors`, `AppTheme.typography`, etc. — the read side of [AppTheme]. */
internal object AppTheme {
    val colors: TallyColors
        @Composable get() = LocalTallyColors.current

    val typography: TallyTypography
        @Composable get() = LocalTallyTypography.current

    val dimens: TallyDimens
        @Composable get() = LocalTallyDimens.current

    val elevation: TallyElevation
        @Composable get() = LocalTallyElevation.current

    val motion: TallyMotion
        @Composable get() = LocalMotion.current
}
