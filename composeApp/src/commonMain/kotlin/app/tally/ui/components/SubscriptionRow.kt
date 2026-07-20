package app.tally.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.ui.theme.AppTheme
import app.tally.ui.theme.withTabularNums
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.subscription_row_delete

/**
 * Monogram · (name + optional badge) · category/due subline · tabular amount
 * + cadence · optional delete button (SubscriptionRow.jsx + AGENTS.md §3).
 * The delete button is a separate sibling focus target — Compose doesn't
 * bubble touch events the way DOM does, so it never triggers [onClick]
 * without any `stopPropagation`-equivalent needed.
 */
@Composable
internal fun TallySubscriptionRow(
    name: String,
    amount: String,
    modifier: Modifier = Modifier,
    category: Category? = null,
    cadence: String = "mo",
    monogramLetter: String? = null,
    status: Status? = null,
    statusLabel: String? = null,
    onClick: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val interactionSource = remember { MutableInteractionSource() }

    var contentModifier = Modifier.sizeIn(minHeight = AppTheme.dimens.tapMin)
    if (onClick != null) {
        contentModifier = contentModifier
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .focusRing()
    }
    contentModifier = contentModifier.padding(horizontal = 4.dp, vertical = 12.dp)

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            modifier = contentModifier.weight(1f), // RowScope.weight — scoped here, inside the outer Row's content.
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TallyMonogramTile(
                category = category ?: Category.OTHER,
                letter = monogramLetter ?: name.firstOrNull()?.toString() ?: "?",
                size = 44.dp,
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                // Name gets its own full-width line — putting the status badge beside it
                // crowded/could-truncate the name, so the badge moved to the subline below.
                Text(
                    text = name,
                    color = colors.textPrimary,
                    fontWeight = typography.weights.semibold,
                    fontSize = typography.sizes.body,
                    fontFamily = typography.fontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
                // Category intentionally not rendered here for now (kept only for the
                // monogram's tint/icon) — status and category were sharing this subline,
                // and category is being dropped from the row display pending a redesign.
                if (status != null) {
                    TallyStatusBadge(status = status, label = statusLabel)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = amount,
                    style = typography.style(size = typography.sizes.body, weight = typography.weights.semibold)
                        .copy(color = colors.textPrimary)
                        .withTabularNums(),
                )
                Text(
                    text = "/$cadence",
                    color = colors.textTertiary,
                    fontSize = typography.sizes.micro,
                    fontFamily = typography.fontFamily,
                )
            }
        }
        if (onDelete != null) {
            TallyIconButton(
                icon = TallyIcons.trash2,
                label = stringResource(Res.string.subscription_row_delete, name),
                onClick = onDelete,
                variant = IconButtonVariant.GHOST,
            )
        }
    }
}
