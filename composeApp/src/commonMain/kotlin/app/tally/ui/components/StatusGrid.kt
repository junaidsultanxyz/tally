package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Status
import app.tally.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.status_active
import tally.composeapp.generated.resources.status_due
import tally.composeapp.generated.resources.status_overdue
import tally.composeapp.generated.resources.status_paused
import tally.composeapp.generated.resources.status_trial

private const val COLUMNS = 3
private val gridSpacing = 8.dp

/**
 * 3-column single-select grid of the five [Status] options (Edit sheet).
 * Selection uses one consistent accent color, not [Status]'s own per-status
 * badge palette (index.html: `.status-opt.on` is plain accent/accent-soft,
 * distinct from the colored `.pill.*` badges elsewhere in the kit).
 *
 * [LazyVerticalGrid] (not a `Row` per row + an empty `weight(1f)` spacer to fill 5 items
 * into 2×3 columns) — that Row+spacer version measured incorrectly inside a
 * [androidx.compose.material3.ModalBottomSheet] specifically: the last row's zero-content
 * weighted spacer collapsed the two real tiles sharing that row down to sliver width
 * whenever the sheet was at its fully-expanded height, only correcting itself mid-drag
 * when a manual gesture forced a fresh measurement pass. `LazyVerticalGrid`'s column sizing
 * doesn't go through that weighted-Row code path, so it doesn't hit the bug.
 */
@Composable
internal fun StatusGrid(selected: Status, onSelect: (Status) -> Unit, modifier: Modifier = Modifier) {
    val rows = (Status.entries.size + COLUMNS - 1) / COLUMNS
    val gridHeight = AppTheme.dimens.tapMin * rows + gridSpacing * (rows - 1)

    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = modifier.fillMaxWidth().height(gridHeight),
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        verticalArrangement = Arrangement.spacedBy(gridSpacing),
        userScrollEnabled = false,
    ) {
        items(Status.entries) { status ->
            StatusTile(
                status = status,
                selected = status == selected,
                onClick = { onSelect(status) },
            )
        }
    }
}

private fun statusIcon(status: Status): ImageVector = when (status) {
    Status.ACTIVE -> TallyIcons.check
    Status.DUE -> TallyIcons.circle
    Status.OVERDUE -> TallyIcons.alertTriangle
    Status.PAUSED -> TallyIcons.pause
    Status.TRIAL -> TallyIcons.star
}

@Composable
private fun statusLabel(status: Status): String = stringResource(
    when (status) {
        Status.ACTIVE -> Res.string.status_active
        Status.DUE -> Res.string.status_due
        Status.OVERDUE -> Res.string.status_overdue
        Status.PAUSED -> Res.string.status_paused
        Status.TRIAL -> Res.string.status_trial
    },
)

@Composable
private fun StatusTile(status: Status, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .sizeIn(minHeight = AppTheme.dimens.tapMin)
            .clip(shape)
            .background(if (selected) colors.accentSoft else colors.bgSurface, shape)
            .border(1.5.dp, if (selected) colors.accent else colors.borderDefault, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { this.role = Role.RadioButton; this.selected = selected }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = statusIcon(status),
            contentDescription = null,
            tint = if (selected) colors.accentSoftFg else colors.textSecondary,
            modifier = Modifier.sizeIn(maxWidth = 16.dp, maxHeight = 16.dp),
        )
        Text(
            text = statusLabel(status),
            color = if (selected) colors.accentSoftFg else colors.textSecondary,
            fontSize = AppTheme.typography.sizes.caption,
            fontWeight = if (selected) AppTheme.typography.weights.semibold else AppTheme.typography.weights.regular,
            fontFamily = AppTheme.typography.fontFamily,
            maxLines = 1,
        )
    }
}
