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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.add_date_picker_cancel
import tally.composeapp.generated.resources.add_date_picker_confirm

/**
 * Read-only field that opens the Material3 [TimePicker] on tap. Material3 doesn't ship a
 * premade `TimePickerDialog` (unlike [androidx.compose.material3.DatePickerDialog]) — this
 * wraps [TimePicker] in a plain [Dialog] + confirm/cancel row, the standard pattern for it.
 * The closed-field summary renders 24-hour `HH:mm` — locale-neutral, avoiding a hardcoded
 * AM/PM convention; full locale-aware formatting is Phase 4 (i18n hardening) scope.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimeField(label: String, value: LocalTime, onTimeSelected: (LocalTime) -> Unit, modifier: Modifier = Modifier) {
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
                .border(1.5.dp, colors.borderDefault, shape)
                .clickable(interactionSource = interactionSource, indication = null, onClick = { showDialog = true })
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = TallyIcons.calendar, contentDescription = null, tint = colors.textTertiary)
            Text(
                text = "${value.hour.toString().padStart(2, '0')}:${value.minute.toString().padStart(2, '0')}",
                color = colors.textPrimary,
                fontSize = typography.sizes.body,
                fontFamily = typography.fontFamily,
            )
        }
    }

    if (showDialog) {
        val state = rememberTimePickerState(initialHour = value.hour, initialMinute = value.minute, is24Hour = true)
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(AppTheme.dimens.radii.lg), color = colors.bgSurface) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text(stringResource(Res.string.add_date_picker_cancel))
                        }
                        TextButton(onClick = {
                            onTimeSelected(LocalTime(state.hour, state.minute))
                            showDialog = false
                        }) {
                            Text(stringResource(Res.string.add_date_picker_confirm))
                        }
                    }
                }
            }
        }
    }
}
