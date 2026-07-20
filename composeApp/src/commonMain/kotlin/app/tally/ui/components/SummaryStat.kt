package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.TallyElevation
import app.tally.ui.theme.TallyShadowLevel
import app.tally.ui.theme.tallyShadow
import app.tally.ui.theme.withTabularNums

internal enum class SummaryStatTone { DEFAULT, ACCENT, WARNING }

/**
 * Label / value / sublabel with a tone-driven value color (SummaryStat.jsx) — card-style
 * background/border/shadow (`.stat` in index.html: bg-surface, 1px border-subtle,
 * radius-**lg** — deliberately not [TallyCard]'s default radius-xl — shadow-xs, 14/16dp
 * padding). This container was missing entirely until now; the stat rendered as bare text
 * with no visible card at all.
 */
@Composable
internal fun TallySummaryStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
    tone: SummaryStatTone = SummaryStatTone.DEFAULT,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val valueColor = when (tone) {
        SummaryStatTone.DEFAULT -> colors.textPrimary
        SummaryStatTone.ACCENT -> colors.accent
        SummaryStatTone.WARNING -> colors.warningFg
    }
    val shape = RoundedCornerShape(AppTheme.dimens.radii.lg)

    Column(
        modifier = modifier
            .tallyShadow(TallyShadowLevel.XS, TallyElevation(isDark = colors.isDark), shape)
            .clip(shape)
            .background(colors.bgSurface, shape)
            .border(1.dp, colors.borderSubtle, shape)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = colors.textSecondary,
            fontSize = typography.sizes.caption,
            fontWeight = typography.weights.semibold,
            fontFamily = typography.fontFamily,
            letterSpacing = typography.tracking.wide,
        )
        Text(
            text = value,
            style = typography
                .style(
                    size = typography.sizes.title,
                    lineHeightMultiplier = 1.1f,
                    letterSpacing = (-0.01).em,
                    weight = typography.weights.bold,
                )
                .copy(color = valueColor)
                .withTabularNums(),
        )
        if (sublabel != null) {
            Text(
                text = sublabel,
                color = colors.textTertiary,
                fontSize = typography.sizes.caption,
                fontFamily = typography.fontFamily,
            )
        }
    }
}
