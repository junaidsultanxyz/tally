package app.tally.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font as ResFont
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.figtree_bold
import tally.composeapp.generated.resources.figtree_medium
import tally.composeapp.generated.resources.figtree_regular
import tally.composeapp.generated.resources.figtree_semibold

/**
 * Figtree, bundled locally (composeResources/font) at weights 400/500/600/700 —
 * never loaded from a CDN at runtime (AGENTS.md §20). Ported from
 * `Subscription app design system/tokens/fonts.css` and `typography.css`.
 */
@Composable
internal fun tallyFontFamily(): FontFamily = FontFamily(
    ResFont(Res.font.figtree_regular, FontWeight.Normal),
    ResFont(Res.font.figtree_medium, FontWeight.Medium),
    ResFont(Res.font.figtree_semibold, FontWeight.SemiBold),
    ResFont(Res.font.figtree_bold, FontWeight.Bold),
)

/** Base sizes (`--fs-*` in typography.css) before the [scale] multiplier is applied. */
private object BaseFontSizesSp {
    const val DISPLAY = 32f
    const val TITLE = 24f
    const val HEADLINE = 20f
    const val BODY_LG = 17f
    const val BODY = 15f
    const val CALLOUT = 14f
    const val CAPTION = 13f
    const val MICRO = 12f
}

/** `--fs-*` tokens with the runtime Dynamic-Type multiplier ([TextSize]) applied. */
internal data class TallyFontSizes(
    val display: TextUnit,
    val title: TextUnit,
    val headline: TextUnit,
    val bodyLg: TextUnit,
    val body: TextUnit,
    val callout: TextUnit,
    val caption: TextUnit,
    val micro: TextUnit,
)

private fun tallyFontSizes(scale: Float): TallyFontSizes = TallyFontSizes(
    display = (BaseFontSizesSp.DISPLAY * scale).sp,
    title = (BaseFontSizesSp.TITLE * scale).sp,
    headline = (BaseFontSizesSp.HEADLINE * scale).sp,
    bodyLg = (BaseFontSizesSp.BODY_LG * scale).sp,
    body = (BaseFontSizesSp.BODY * scale).sp,
    callout = (BaseFontSizesSp.CALLOUT * scale).sp,
    caption = (BaseFontSizesSp.CAPTION * scale).sp,
    micro = (BaseFontSizesSp.MICRO * scale).sp,
)

/** `--lh-*` tokens — multipliers applied to a text style's font size, not absolute values. */
internal data class TallyLineHeights(
    val tight: Float = 1.15f,
    val snug: Float = 1.3f,
    val normal: Float = 1.5f,
)

/** `--tracking-*` tokens. */
internal data class TallyTracking(
    val tight: TextUnit = (-0.02).em,
    val normal: TextUnit = 0.em,
    val wide: TextUnit = 0.02.em,
)

/**
 * `--fw-*` tokens, resolved through bold-text mode: when [boldText] is on,
 * `--fw-regular` (400) bumps to 600 and `--fw-medium` (500) bumps to 700 —
 * matching the kit's bold-text accessibility mode exactly.
 */
internal data class TallyFontWeights(
    val regular: FontWeight,
    val medium: FontWeight,
    val semibold: FontWeight = FontWeight.SemiBold,
    val bold: FontWeight = FontWeight.Bold,
)

private fun tallyFontWeights(boldText: Boolean): TallyFontWeights =
    if (boldText) {
        TallyFontWeights(regular = FontWeight.SemiBold, medium = FontWeight.Bold)
    } else {
        TallyFontWeights(regular = FontWeight.Normal, medium = FontWeight.Medium)
    }

/**
 * The full typography token set for one composition: sizes already scaled by
 * [TextSize], weights already resolved for bold-text mode, plus the raw
 * line-height/tracking multipliers and the loaded [fontFamily]. Components
 * (Phase 0.3) combine these into concrete `TextStyle`s per their own role —
 * this layer intentionally does not decide which line-height or tracking a
 * given text role uses, since the token files don't fix that mapping either.
 */
internal data class TallyTypography(
    val fontFamily: FontFamily,
    val sizes: TallyFontSizes,
    val lineHeights: TallyLineHeights,
    val tracking: TallyTracking,
    val weights: TallyFontWeights,
) {
    /** Convenience: a plain-regular [TextStyle] at [size] with [lineHeightMultiplier] × size line height. */
    fun style(
        size: TextUnit,
        lineHeightMultiplier: Float = lineHeights.normal,
        letterSpacing: TextUnit = tracking.normal,
        weight: FontWeight = weights.regular,
    ): TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = weight,
        fontSize = size,
        lineHeight = (size.value * lineHeightMultiplier).sp,
        letterSpacing = letterSpacing,
    )
}

/**
 * `font-variant-numeric: tabular-nums` for money/amount text so digit columns
 * align (SummaryStat, SubscriptionRow amounts). Confirmed Figtree ships a
 * `tnum` OpenType feature table, so this isn't a monospace-digit fallback.
 */
internal fun TextStyle.withTabularNums(): TextStyle = merge(TextStyle(fontFeatureSettings = "tnum"))

@Composable
internal fun rememberTallyTypography(scale: Float, boldText: Boolean): TallyTypography = TallyTypography(
    fontFamily = tallyFontFamily(),
    sizes = tallyFontSizes(scale),
    lineHeights = TallyLineHeights(),
    tracking = TallyTracking(),
    weights = tallyFontWeights(boldText),
)
