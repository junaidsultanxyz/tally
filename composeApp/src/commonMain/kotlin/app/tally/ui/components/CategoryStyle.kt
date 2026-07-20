package app.tally.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.tally.domain.model.Category
import app.tally.ui.theme.AppTheme
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.category_entertainment
import tally.composeapp.generated.resources.category_fitness
import tally.composeapp.generated.resources.category_gaming
import tally.composeapp.generated.resources.category_music
import tally.composeapp.generated.resources.category_news
import tally.composeapp.generated.resources.category_other
import tally.composeapp.generated.resources.category_productivity
import tally.composeapp.generated.resources.category_storage

internal data class CategoryTint(val bg: Color, val fg: Color)

/** Ported from the kit's `tint` map (index.html) — Gaming/Other aren't in the kit's map, so they fall back to accent-soft like the kit's own unlisted-category fallback. */
@Composable
internal fun categoryTint(category: Category): CategoryTint {
    val colors = AppTheme.colors
    return when (category) {
        Category.ENTERTAINMENT -> CategoryTint(colors.dangerBg, colors.dangerFg)
        Category.MUSIC -> CategoryTint(colors.successBg, colors.successFg)
        Category.STORAGE -> CategoryTint(colors.infoBg, colors.infoFg)
        Category.PRODUCTIVITY -> CategoryTint(colors.accentSoft, colors.accentSoftFg)
        Category.FITNESS -> CategoryTint(colors.warningBg, colors.warningFg)
        Category.NEWS -> CategoryTint(colors.bgSunken, colors.textSecondary)
        Category.GAMING, Category.OTHER -> CategoryTint(colors.accentSoft, colors.accentSoftFg)
    }
}

internal fun categoryIcon(category: Category): ImageVector = when (category) {
    Category.ENTERTAINMENT -> TallyIcons.clapperboard
    Category.MUSIC -> TallyIcons.music
    Category.PRODUCTIVITY -> TallyIcons.bot // displayed as "AI" (categoryLabel) — enum name kept to avoid a data migration.
    Category.STORAGE -> TallyIcons.cloud
    Category.FITNESS -> TallyIcons.dumbbell
    Category.NEWS -> TallyIcons.newspaper
    Category.GAMING -> TallyIcons.gamepad2
    Category.OTHER -> TallyIcons.tag
}

@Composable
internal fun categoryLabel(category: Category): String = stringResource(
    when (category) {
        Category.ENTERTAINMENT -> Res.string.category_entertainment
        Category.MUSIC -> Res.string.category_music
        Category.PRODUCTIVITY -> Res.string.category_productivity
        Category.STORAGE -> Res.string.category_storage
        Category.FITNESS -> Res.string.category_fitness
        Category.NEWS -> Res.string.category_news
        Category.GAMING -> Res.string.category_gaming
        Category.OTHER -> Res.string.category_other
    },
)
