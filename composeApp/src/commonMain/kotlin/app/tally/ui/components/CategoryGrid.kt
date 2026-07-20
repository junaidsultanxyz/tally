package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Category
import app.tally.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.add_category_grid_entertainment

/**
 * 4×2 single-select grid of the 8 categories (Add form, Edit sheet). One
 * consistent accent color marks the selection regardless of which category
 * is picked — a per-category background here read as visual noise in a
 * picker; category tints stay reserved for monogram tiles in list rows,
 * where distinguishing between subscriptions at a glance is the point.
 */
@Composable
internal fun CategoryGrid(selected: Category, onSelect: (Category) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Category.entries.chunked(4).forEach { rowCategories ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCategories.forEach { category ->
                    CategoryTile(
                        category = category,
                        selected = category == selected,
                        onClick = { onSelect(category) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTile(category: Category, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)
    val interactionSource = remember { MutableInteractionSource() }
    // categoryLabel's full "Entertainment" truncates in a 4-column grid tile — "Fun" is the
    // kit's own abbreviation for this one tile (index.html's Add-screen cat-grid).
    val label = if (category == Category.ENTERTAINMENT) {
        stringResource(Res.string.add_category_grid_entertainment)
    } else {
        categoryLabel(category)
    }

    Column(
        modifier = modifier
            .sizeIn(minHeight = 64.dp)
            .clip(shape)
            .background(if (selected) colors.accentSoft else colors.bgSurface, shape)
            .border(1.5.dp, if (selected) colors.accent else colors.borderDefault, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { this.role = Role.RadioButton; this.selected = selected }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = categoryIcon(category),
            contentDescription = null,
            tint = if (selected) colors.accentSoftFg else colors.textSecondary,
        )
        Text(
            text = label,
            color = if (selected) colors.accentSoftFg else colors.textSecondary,
            fontSize = AppTheme.typography.sizes.caption,
            fontWeight = if (selected) AppTheme.typography.weights.semibold else AppTheme.typography.weights.regular,
            fontFamily = AppTheme.typography.fontFamily,
            maxLines = 1,
        )
    }
}
