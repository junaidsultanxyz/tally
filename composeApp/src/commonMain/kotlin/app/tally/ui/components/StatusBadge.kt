package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.tally.domain.model.Status
import app.tally.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.status_active
import tally.composeapp.generated.resources.status_due
import tally.composeapp.generated.resources.status_overdue
import tally.composeapp.generated.resources.status_paused
import tally.composeapp.generated.resources.status_trial

private data class StatusBadgeSpec(val fg: Color, val bg: Color, val icon: ImageVector, val label: String)

@Composable
private fun statusBadgeSpec(status: Status): StatusBadgeSpec {
    val colors = AppTheme.colors
    return when (status) {
        Status.ACTIVE -> StatusBadgeSpec(colors.successFg, colors.successBg, TallyIcons.check, stringResource(Res.string.status_active))
        Status.DUE -> StatusBadgeSpec(colors.warningFg, colors.warningBg, TallyIcons.circle, stringResource(Res.string.status_due))
        Status.OVERDUE -> StatusBadgeSpec(colors.dangerFg, colors.dangerBg, TallyIcons.alertTriangle, stringResource(Res.string.status_overdue))
        Status.PAUSED -> StatusBadgeSpec(colors.infoFg, colors.infoBg, TallyIcons.pause, stringResource(Res.string.status_paused))
        Status.TRIAL -> StatusBadgeSpec(colors.accentSoftFg, colors.accentSoft, TallyIcons.star, stringResource(Res.string.status_trial))
    }
}

/**
 * Status is **never** color-only (colorblind safety, colors.css) — always
 * icon + label, no icon-less mode (StatusBadge.d.ts: "Keep true"). [label]
 * overrides the default status word (e.g. "Due 3d" instead of "Due").
 */
@Composable
internal fun TallyStatusBadge(status: Status, modifier: Modifier = Modifier, label: String? = null) {
    val spec = statusBadgeSpec(status)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppTheme.dimens.radii.pill))
            .background(spec.bg, RoundedCornerShape(AppTheme.dimens.radii.pill))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = spec.icon,
            contentDescription = null,
            tint = spec.fg,
            // ~0.9em of caption size (13sp * 0.9 ≈ 12dp), per StatusBadge.jsx's `fontSize: "0.9em"` glyph.
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = label ?: spec.label,
            color = spec.fg,
            fontSize = AppTheme.typography.sizes.caption,
            fontWeight = AppTheme.typography.weights.semibold,
            fontFamily = AppTheme.typography.fontFamily,
            lineHeight = (AppTheme.typography.sizes.caption.value * 1.2f).sp,
        )
    }
}
