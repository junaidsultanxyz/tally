package app.tally.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * `--shadow-*` tokens from `tokens/shadows.css`. Light shadows are warm-tinted
 * (oklch(0.30 0.02 95) at varying alpha); dark shadows are plain black. Light
 * `sm`/`md`/`lg` are each two stacked CSS layers — `xs` and every dark level
 * are one layer.
 *
 * **Approximation note:** Compose's [shadow] modifier has no direct CSS
 * `box-shadow` equivalent (no separate offset/blur-radius/spread inputs) — it
 * takes a single elevation that the platform's own shadow renderer (View
 * elevation on Android, Skia on iOS/desktop) turns into a soft downward
 * shadow. Each CSS layer here is approximated by one `.shadow()` call using
 * that layer's blur radius as the elevation and its own color (alpha
 * included) as both ambient and spot color; multiple layers are stacked by
 * chaining `.shadow()` calls. This reproduces the intensity/spread
 * relationship reasonably well without a hand-rolled blur or an
 * API 31+ RenderEffect dependency (this app's minSdk is 26).
 */
internal enum class TallyShadowLevel { XS, SM, MD, LG }

private data class ShadowLayer(val blurRadius: Dp, val color: Color)

private val lightShadowTint = Color(0xFF312E22) // oklch(0.30 0.02 95)

private fun lightTint(alpha: Float) = lightShadowTint.copy(alpha = alpha)
private fun darkTint(alpha: Float) = Color.Black.copy(alpha = alpha)

private val lightShadowLayers: Map<TallyShadowLevel, List<ShadowLayer>> = mapOf(
    TallyShadowLevel.XS to listOf(ShadowLayer(2.dp, lightTint(0.06f))),
    TallyShadowLevel.SM to listOf(
        ShadowLayer(3.dp, lightTint(0.08f)),
        ShadowLayer(2.dp, lightTint(0.05f)),
    ),
    TallyShadowLevel.MD to listOf(
        ShadowLayer(12.dp, lightTint(0.08f)),
        ShadowLayer(4.dp, lightTint(0.05f)),
    ),
    TallyShadowLevel.LG to listOf(
        ShadowLayer(32.dp, lightTint(0.12f)),
        ShadowLayer(8.dp, lightTint(0.06f)),
    ),
)

private val darkShadowLayers: Map<TallyShadowLevel, List<ShadowLayer>> = mapOf(
    TallyShadowLevel.XS to listOf(ShadowLayer(2.dp, darkTint(0.30f))),
    TallyShadowLevel.SM to listOf(ShadowLayer(3.dp, darkTint(0.40f))),
    TallyShadowLevel.MD to listOf(ShadowLayer(12.dp, darkTint(0.45f))),
    TallyShadowLevel.LG to listOf(ShadowLayer(32.dp, darkTint(0.55f))),
)

internal data class TallyElevation(val isDark: Boolean)

/** Applies the (possibly two-layer) shadow for [level] under the given [shape]. */
internal fun Modifier.tallyShadow(
    level: TallyShadowLevel,
    elevation: TallyElevation,
    shape: Shape = RoundedCornerShape(0.dp),
): Modifier {
    val layers = if (elevation.isDark) darkShadowLayers.getValue(level) else lightShadowLayers.getValue(level)
    var result = this
    for (layer in layers) {
        result = result.shadow(
            elevation = layer.blurRadius,
            shape = shape,
            ambientColor = layer.color,
            spotColor = layer.color,
        )
    }
    return result
}
