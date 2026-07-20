package app.tally.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.tally.data.settings.AppearanceSettings
import app.tally.data.settings.TextSize
import app.tally.money.Currencies
import app.tally.ui.components.TallyIconButton
import app.tally.ui.components.TallyIcons
import app.tally.ui.components.TallyListCard
import app.tally.ui.components.TallySegmentedControl
import app.tally.ui.components.TallySwitch
import app.tally.ui.components.TimeField
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tally.composeapp.generated.resources.Res
import tally.composeapp.generated.resources.settings_account_placeholder
import tally.composeapp.generated.resources.settings_bold_text
import tally.composeapp.generated.resources.settings_colorblind_labels
import tally.composeapp.generated.resources.settings_currency
import tally.composeapp.generated.resources.settings_currency_display
import tally.composeapp.generated.resources.settings_dark_mode
import tally.composeapp.generated.resources.settings_dark_mode_follow_system
import tally.composeapp.generated.resources.settings_default_lead_days
import tally.composeapp.generated.resources.settings_default_reminder_time
import tally.composeapp.generated.resources.settings_high_contrast
import tally.composeapp.generated.resources.settings_large_tap_targets
import tally.composeapp.generated.resources.settings_lead_days_decrease
import tally.composeapp.generated.resources.settings_lead_days_increase
import tally.composeapp.generated.resources.settings_lead_days_value
import tally.composeapp.generated.resources.settings_reduce_motion
import tally.composeapp.generated.resources.settings_section_account
import tally.composeapp.generated.resources.settings_section_appearance
import tally.composeapp.generated.resources.settings_section_motion_vision
import tally.composeapp.generated.resources.settings_section_reminders
import tally.composeapp.generated.resources.settings_section_regional
import tally.composeapp.generated.resources.settings_section_text_touch
import tally.composeapp.generated.resources.settings_text_size
import tally.composeapp.generated.resources.settings_text_size_a
import tally.composeapp.generated.resources.settings_text_size_a_plus
import tally.composeapp.generated.resources.settings_text_size_a_plus_plus
import tally.composeapp.generated.resources.settings_title

/**
 * Settings / Accessibility (FUNCTIONALITIES.md §8 / IMPLEMENTATION_PLAN.md 2.7). Every
 * control writes straight through [SettingsViewModel] → `SettingsRepository` → DataStore;
 * [app.tally.ui.theme.AppTheme] observes the same flow at the app root, so a change here is
 * visible on every screen in one frame — no manual refresh, no "Apply" button.
 */
@Composable
internal fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val colors = AppTheme.colors
    val typography = AppTheme.typography

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(AppTheme.dimens.gutter),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            Text(
                text = stringResource(Res.string.settings_title),
                color = colors.textPrimary,
                fontSize = typography.sizes.title,
                fontWeight = typography.weights.bold,
                fontFamily = typography.fontFamily,
            )
        }

        item {
            Section(stringResource(Res.string.settings_section_appearance)) {
                TallyListCard(
                    rows = listOf(
                        { DarkModeRow(uiState.appearance, viewModel::setDarkMode) },
                        {
                            TallySwitch(
                                checked = uiState.appearance.highContrast,
                                onCheckedChange = viewModel::setHighContrast,
                                label = stringResource(Res.string.settings_high_contrast),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                    ),
                )
            }
        }

        item {
            Section(stringResource(Res.string.settings_section_regional)) {
                TallyListCard(rows = listOf { CurrencyRow(uiState.activeCurrency, viewModel::setActiveCurrency) })
            }
        }

        item {
            Section(stringResource(Res.string.settings_section_text_touch)) {
                TallyListCard(
                    rows = listOf(
                        { TextSizeRow(uiState.appearance.textSize, viewModel::setTextSize) },
                        {
                            TallySwitch(
                                checked = uiState.appearance.largeTap,
                                onCheckedChange = viewModel::setLargeTap,
                                label = stringResource(Res.string.settings_large_tap_targets),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                        {
                            TallySwitch(
                                checked = uiState.appearance.boldText,
                                onCheckedChange = viewModel::setBoldText,
                                label = stringResource(Res.string.settings_bold_text),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                    ),
                )
            }
        }

        item {
            Section(stringResource(Res.string.settings_section_motion_vision)) {
                TallyListCard(
                    rows = listOf(
                        {
                            TallySwitch(
                                checked = uiState.appearance.reducedMotion,
                                onCheckedChange = viewModel::setReducedMotion,
                                label = stringResource(Res.string.settings_reduce_motion),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                        {
                            TallySwitch(
                                checked = uiState.appearance.colorblindLabels,
                                onCheckedChange = viewModel::setColorblindLabels,
                                label = stringResource(Res.string.settings_colorblind_labels),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                    ),
                )
            }
        }

        item {
            Section(stringResource(Res.string.settings_section_reminders)) {
                TallyListCard(
                    rows = listOf(
                        {
                            LeadDaysRow(
                                days = uiState.defaultReminderLeadDays,
                                onDecrement = viewModel::decrementLeadDays,
                                onIncrement = viewModel::incrementLeadDays,
                            )
                        },
                        {
                            TimeField(
                                label = stringResource(Res.string.settings_default_reminder_time),
                                value = uiState.defaultReminderTime,
                                onTimeSelected = viewModel::setDefaultReminderTime,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        },
                    ),
                )
            }
        }

        item {
            Section(stringResource(Res.string.settings_section_account)) {
                TallyListCard(
                    rows = listOf {
                        Text(
                            text = stringResource(Res.string.settings_account_placeholder),
                            color = colors.textTertiary,
                            fontSize = typography.sizes.body,
                            fontFamily = typography.fontFamily,
                            modifier = Modifier.padding(vertical = 12.dp),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            color = colors.textSecondary,
            fontSize = typography.sizes.callout,
            fontWeight = typography.weights.semibold,
            fontFamily = typography.fontFamily,
            letterSpacing = typography.tracking.wide,
        )
        content()
    }
}

@Composable
private fun DarkModeRow(appearance: AppearanceSettings, onSetDarkMode: (Boolean) -> Unit) {
    val followsSystem = appearance.darkMode == null
    val effectiveDark = appearance.darkMode ?: isSystemInDarkTheme()
    TallySwitch(
        checked = effectiveDark,
        onCheckedChange = onSetDarkMode,
        label = stringResource(Res.string.settings_dark_mode),
        description = if (followsSystem) stringResource(Res.string.settings_dark_mode_follow_system) else null,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun CurrencyRow(selected: String, onSelect: (String) -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    var expanded by remember { mutableStateOf(false) }
    val currency = Currencies.get(selected)

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings_currency),
                color = colors.textPrimary,
                fontSize = typography.sizes.body,
                fontWeight = typography.weights.medium,
                fontFamily = typography.fontFamily,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(Res.string.settings_currency_display, currency.code, currency.symbol),
                    color = colors.textSecondary,
                    fontSize = typography.sizes.body,
                    fontFamily = typography.fontFamily,
                )
                Icon(imageVector = TallyIcons.chevronRight, contentDescription = null, tint = colors.textTertiary)
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Currencies.all.forEach { c ->
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.settings_currency_display, c.code, c.symbol)) },
                    onClick = { onSelect(c.code); expanded = false },
                )
            }
        }
    }
}

@Composable
private fun TextSizeRow(selected: TextSize, onSelect: (TextSize) -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val labels = mapOf(
        TextSize.NORMAL to stringResource(Res.string.settings_text_size_a),
        TextSize.LARGE to stringResource(Res.string.settings_text_size_a_plus),
        TextSize.X_LARGE to stringResource(Res.string.settings_text_size_a_plus_plus),
    )
    Column(modifier = Modifier.padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(Res.string.settings_text_size),
            color = colors.textPrimary,
            fontSize = typography.sizes.body,
            fontWeight = typography.weights.medium,
            fontFamily = typography.fontFamily,
        )
        TallySegmentedControl(
            options = listOf(TextSize.NORMAL, TextSize.LARGE, TextSize.X_LARGE),
            selected = selected,
            onSelect = onSelect,
            labelOf = { labels.getValue(it) },
        )
    }
}

@Composable
private fun LeadDaysRow(days: Int, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    val colors = AppTheme.colors
    val typography = AppTheme.typography
    val decreaseLabel = stringResource(Res.string.settings_lead_days_decrease)
    val increaseLabel = stringResource(Res.string.settings_lead_days_increase)
    val valueText = pluralStringResource(Res.plurals.settings_lead_days_value, days, days)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.settings_default_lead_days),
            color = colors.textPrimary,
            fontSize = typography.sizes.body,
            fontWeight = typography.weights.medium,
            fontFamily = typography.fontFamily,
            modifier = Modifier.weight(1f),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            TallyIconButton(icon = TallyIcons.minus, label = decreaseLabel, onClick = onDecrement)
            Text(
                text = valueText,
                color = colors.textPrimary,
                fontSize = typography.sizes.body,
                fontFamily = typography.fontFamily,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            TallyIconButton(icon = TallyIcons.plus, label = increaseLabel, onClick = onIncrement)
        }
    }
}
