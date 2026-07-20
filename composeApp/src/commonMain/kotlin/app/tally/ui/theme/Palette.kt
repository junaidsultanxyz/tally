package app.tally.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Raw color ramps ported from `Subscription app design system/tokens/colors.css`.
 * Values are pre-converted from OKLCH to sRGB (Compose has no OKLCH color space
 * support); the OKLCH source is kept in each comment so the mapping can be
 * re-verified against the CSS at any time. Do not use these directly in UI —
 * go through [TallyColors] semantic aliases instead.
 */
internal object Palette {
    // --- Warm sage accent ramp (primary) ---
    val sage50 = Color(0xFFECF8EE) // oklch(0.968 0.018 150)
    val sage100 = Color(0xFFDBEEDE) // oklch(0.930 0.030 150)
    val sage200 = Color(0xFFC2DFC7) // oklch(0.875 0.045 150)
    val sage300 = Color(0xFFA5C8AB) // oklch(0.800 0.055 150)
    val sage400 = Color(0xFF89B090) // oklch(0.720 0.062 150)
    val sage500 = Color(0xFF6F9877) // oklch(0.640 0.066 150)
    val sage600 = Color(0xFF597E60) // oklch(0.555 0.062 150)
    val sage700 = Color(0xFF44624A) // oklch(0.465 0.052 150)
    val sage800 = Color(0xFF344A38) // oklch(0.385 0.040 150)
    val sage900 = Color(0xFF253528) // oklch(0.310 0.030 150)

    // --- Warm apricot secondary (friendly highlight) ---
    val apricot100 = Color(0xFFFDE7CF) // oklch(0.940 0.040 70)
    val apricot300 = Color(0xFFF2C89C) // oklch(0.860 0.075 68)
    val apricot500 = Color(0xFFE5AE78) // oklch(0.790 0.095 66)
    val apricot700 = Color(0xFFB47F52) // oklch(0.640 0.090 60)

    // --- Warm neutral gray ramp ---
    val neutral0 = Color(0xFFFFFFFF) // oklch(1 0 0)
    val neutral25 = Color(0xFFFCFCF9) // oklch(0.990 0.003 95)
    val neutral50 = Color(0xFFF8F8F5) // oklch(0.978 0.004 95)
    val neutral100 = Color(0xFFF2F1ED) // oklch(0.958 0.005 95)
    val neutral200 = Color(0xFFE6E5E1) // oklch(0.922 0.006 95)
    val neutral300 = Color(0xFFD8D7D2) // oklch(0.878 0.006 95)
    val neutral400 = Color(0xFFB2B1AC) // oklch(0.760 0.007 95)
    val neutral500 = Color(0xFF8A8984) // oklch(0.630 0.008 95)
    val neutral600 = Color(0xFF6A6964) // oklch(0.520 0.008 95)
    val neutral700 = Color(0xFF494843) // oklch(0.400 0.008 95)
    val neutral800 = Color(0xFF2F2E2A) // oklch(0.300 0.007 95)
    val neutral900 = Color(0xFF1B1B17) // oklch(0.220 0.006 95)
    val neutral950 = Color(0xFF100F0C) // oklch(0.170 0.006 95)

    // --- Semantic hues, light theme (muted, always icon+label paired — never color-only) ---
    val successFgLight = Color(0xFF317A45) // oklch(0.520 0.110 150)
    val successBgLight = Color(0xFFDCF2DF) // oklch(0.940 0.035 150)
    val warningFgLight = Color(0xFF9D671C) // oklch(0.560 0.110 70)
    val warningBgLight = Color(0xFFFFE9C8) // oklch(0.945 0.050 78)
    val dangerFgLight = Color(0xFFB84D49) // oklch(0.560 0.140 25)
    val dangerBgLight = Color(0xFFFFE6E2) // oklch(0.950 0.035 28)
    val infoFgLight = Color(0xFF3D74A0) // oklch(0.540 0.090 245)
    val infoBgLight = Color(0xFFDDF0FF) // oklch(0.945 0.030 245)

    // --- Semantic hues, dark theme ---
    val successFgDark = Color(0xFF88CA95) // oklch(0.780 0.100 150)
    val successBgDark = Color(0xFF243C29) // oklch(0.330 0.045 150)
    val warningFgDark = Color(0xFFE8BC78) // oklch(0.820 0.100 78)
    val warningBgDark = Color(0xFF463518) // oklch(0.340 0.050 78)
    val dangerFgDark = Color(0xFFF49286) // oklch(0.760 0.120 28)
    val dangerBgDark = Color(0xFF4E2A25) // oklch(0.330 0.055 28)
    val infoFgDark = Color(0xFF86B7E1) // oklch(0.760 0.080 245)
    val infoBgDark = Color(0xFF1E3548) // oklch(0.320 0.045 245)
}
