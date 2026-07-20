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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.add_date_picker_cancel
import tally.composeapp.generated.resources.add_date_picker_confirm

/**
 * Read-only field that opens the CMP Material3 [DatePickerDialog] on tap — the
 * FUNCTIONALITIES.md kit uses a browser `<input type="date">`; the plan explicitly allows
 * "the CMP date picker if it meets both platforms' UX bar" (IMPLEMENTATION_PLAN.md 2.4/2.5).
 * The closed-field summary renders as ISO-8601 (`YYYY-MM-DD`) rather than a hand-rolled
 * English month name, to avoid a locale-incorrect date format — full locale-aware display
 * formatting is Phase 4 (i18n hardening) scope; the picker dialog itself is already
 * locale-aware since it comes from the platform's Material3 implementation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateField(
    label: String,
    value: LocalDate?,
    placeholder: String,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val shape = RoundedCornerShape(AppTheme.dimens.radii.md)
    val interactionSource = remember { MutableInteractionSource() }
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = colors.textSecondary,
            fontSize = typography.sizes.callout,
            fontWeight = typography.weights.semibold,
            fontFamily = typography.fontFamily,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = AppTheme.dimens.tapMin)
                .clip(shape)
                .background(colors.bgSurface, shape)
                .border(1.5.dp, if (error != null) colors.dangerFg else colors.borderDefault, shape)
                .clickable(interactionSource = interactionSource, indication = null, onClick = { showDialog = true })
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = TallyIcons.calendar, contentDescription = null, tint = colors.textTertiary)
            Text(
                text = value?.toString() ?: placeholder,
                color = if (value != null) colors.textPrimary else colors.textTertiary,
                fontSize = typography.sizes.body,
                fontFamily = typography.fontFamily,
            )
        }
        if (error != null) {
            Text(
                text = error,
                color = colors.dangerFg,
                fontSize = typography.sizes.caption,
                fontFamily = typography.fontFamily,
            )
        }
    }

    if (showDialog) {
        val initialMillis = value?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date)
                    }
                    showDialog = false
                }) {
                    Text(stringResource(Res.string.add_date_picker_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.add_date_picker_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
