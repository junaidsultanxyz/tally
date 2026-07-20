package app.tally.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.error as semanticsError
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme

/** Label + input row (with optional leading icon and/or text prefix, e.g. a currency symbol) + hint/error (TextField.jsx). */
@Composable
internal fun TallyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    prefix: String? = null,
    hint: String? = null,
    error: String? = null,
    disabled: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val colors = AppTheme.colors
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) {
            Text(
                text = label,
                color = colors.textSecondary,
                fontSize = AppTheme.typography.sizes.callout,
                fontWeight = AppTheme.typography.weights.semibold,
                fontFamily = AppTheme.typography.fontFamily,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = AppTheme.dimens.tapMin)
                .clip(shape)
                .background(colors.bgSurface, shape)
                .border(1.5.dp, if (error != null) colors.dangerFg else colors.borderDefault, shape)
                .alpha(if (disabled) 0.5f else 1f)
                .padding(horizontal = 14.dp)
                .semantics { if (error != null) semanticsError(error) },
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(imageVector = leadingIcon, contentDescription = null, tint = colors.textTertiary)
            }
            if (prefix != null) {
                Text(
                    text = prefix,
                    color = colors.textTertiary,
                    fontWeight = AppTheme.typography.weights.semibold,
                    fontSize = AppTheme.typography.sizes.body,
                    fontFamily = AppTheme.typography.fontFamily,
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = colors.textTertiary,
                        fontSize = AppTheme.typography.sizes.body,
                        fontFamily = AppTheme.typography.fontFamily,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    // Without this, the field's tap/focus hit-region is only as wide as its
                    // current text content — tapping the empty space to the right of short
                    // text (inside the same visual row) silently did nothing.
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !disabled,
                    textStyle = TextStyle(
                        color = colors.textPrimary,
                        fontSize = AppTheme.typography.sizes.body,
                        fontFamily = AppTheme.typography.fontFamily,
                    ),
                    cursorBrush = SolidColor(colors.accent),
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                )
            }
        }
        val footer = error ?: hint
        if (footer != null) {
            Text(
                text = footer,
                color = if (error != null) colors.dangerFg else colors.textTertiary,
                fontSize = AppTheme.typography.sizes.caption,
                fontFamily = AppTheme.typography.fontFamily,
            )
        }
    }
}
