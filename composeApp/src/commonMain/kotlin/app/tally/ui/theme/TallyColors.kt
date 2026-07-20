package app.tally.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Semantic color aliases — mirrors the `--bg-*` / `--text-*` / `--border-*` /
 * `--accent-*` / semantic-hue custom properties in
 * `Subscription app design system/tokens/colors.css` and the
 * `[data-contrast="high"]` overrides in `accessibility.css`. UI code reads
 * colors exclusively through [LocalTallyColors] / [AppTheme] — never [Palette]
 * directly, and never a hardcoded literal.
 */
internal data class TallyColors(
    /** True for [DarkTallyColors]/[DarkHighContrastTallyColors] — lets components (e.g. shadow tint) branch without comparing color values. */
    val isDark: Boolean,
    val bgApp: Color,
    val bgSurface: Color,
    val bgElevated: Color,
    val bgSunken: Color,
    val bgHover: Color,
    val bgActive: Color,

    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textOnAccent: Color,
    val textDisabled: Color,

    val borderSubtle: Color,
    val borderDefault: Color,
    val borderStrong: Color,

    val accent: Color,
    val accentHover: Color,
    val accentPressed: Color,
    val accentSoft: Color,
    val accentSoftFg: Color,
    val accentRing: Color,

    val focusRing: Color,

    val successFg: Color,
    val successBg: Color,
    val warningFg: Color,
    val warningBg: Color,
    val dangerFg: Color,
    val dangerBg: Color,
    val infoFg: Color,
    val infoBg: Color,
)

internal val LightTallyColors = TallyColors(
    isDark = false,
    bgApp = Palette.neutral50,
    bgSurface = Palette.neutral0,
    bgElevated = Palette.neutral0,
    bgSunken = Palette.neutral100,
    bgHover = Palette.neutral100,
    bgActive = Palette.neutral200,

    textPrimary = Palette.neutral900,
    textSecondary = Palette.neutral600,
    textTertiary = Palette.neutral500,
    textOnAccent = Palette.neutral0,
    textDisabled = Palette.neutral400,

    borderSubtle = Palette.neutral200,
    borderDefault = Palette.neutral300,
    borderStrong = Palette.neutral400,

    accent = Palette.sage600,
    accentHover = Palette.sage700,
    accentPressed = Palette.sage800,
    accentSoft = Palette.sage100,
    accentSoftFg = Palette.sage800,
    accentRing = Color(0x666F9877), // oklch(0.640 0.066 150 / 0.40)

    focusRing = Palette.sage600,

    successFg = Palette.successFgLight,
    successBg = Palette.successBgLight,
    warningFg = Palette.warningFgLight,
    warningBg = Palette.warningBgLight,
    dangerFg = Palette.dangerFgLight,
    dangerBg = Palette.dangerBgLight,
    infoFg = Palette.infoFgLight,
    infoBg = Palette.infoBgLight,
)

internal val DarkTallyColors = TallyColors(
    isDark = true,
    bgApp = Palette.neutral950,
    bgSurface = Palette.neutral900,
    bgElevated = Color(0xFF24231F), // oklch(0.255 0.007 95)
    bgSunken = Palette.neutral950,
    bgHover = Color(0xFF2A2924), // oklch(0.280 0.008 95)
    bgActive = Color(0xFF373531), // oklch(0.330 0.008 95)

    textPrimary = Palette.neutral50,
    textSecondary = Palette.neutral400,
    textTertiary = Palette.neutral500,
    textOnAccent = Palette.neutral950,
    textDisabled = Palette.neutral600,

    borderSubtle = Color(0xFF2F2E2A), // oklch(0.300 0.007 95)
    borderDefault = Color(0xFF3C3B36), // oklch(0.350 0.008 95)
    borderStrong = Color(0xFF54534E), // oklch(0.440 0.008 95)

    accent = Palette.sage400,
    accentHover = Palette.sage300,
    accentPressed = Palette.sage200,
    accentSoft = Color(0xFF263B2A), // oklch(0.330 0.040 150)
    accentSoftFg = Palette.sage200,
    accentRing = Color(0x7389B090), // oklch(0.720 0.062 150 / 0.45)

    focusRing = Palette.sage400,

    successFg = Palette.successFgDark,
    successBg = Palette.successBgDark,
    warningFg = Palette.warningFgDark,
    warningBg = Palette.warningBgDark,
    dangerFg = Palette.dangerFgDark,
    dangerBg = Palette.dangerBgDark,
    infoFg = Palette.infoFgDark,
    infoBg = Palette.infoBgDark,
)

// [data-contrast="high"] on top of light — everything not listed here is unchanged from LightTallyColors.
internal val LightHighContrastTallyColors = LightTallyColors.copy(
    textPrimary = Palette.neutral950,
    textSecondary = Palette.neutral800,
    textTertiary = Palette.neutral700,
    borderSubtle = Palette.neutral400,
    borderDefault = Palette.neutral500,
    borderStrong = Palette.neutral700,
    accent = Palette.sage700,
    accentHover = Palette.sage800,
    accentSoftFg = Palette.sage900,
)

// [data-theme="dark"][data-contrast="high"] on top of dark — everything not listed here is unchanged from DarkTallyColors.
internal val DarkHighContrastTallyColors = DarkTallyColors.copy(
    textPrimary = Palette.neutral0,
    textSecondary = Palette.neutral200,
    textTertiary = Palette.neutral300,
    borderSubtle = Palette.neutral500,
    borderDefault = Palette.neutral400,
    borderStrong = Palette.neutral300,
    accent = Palette.sage300,
    accentHover = Palette.sage200,
    accentSoft = Color(0xFF29452F), // oklch(0.360 0.050 150)
    accentSoftFg = Palette.sage100,
)
