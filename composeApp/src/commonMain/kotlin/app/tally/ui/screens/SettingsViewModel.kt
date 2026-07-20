package app.tally.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tally.data.settings.AppearanceSettings
import app.tally.data.settings.SettingsRepository
import app.tally.data.settings.TextSize
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime

public data class SettingsUiState(
    val appearance: AppearanceSettings,
    val activeCurrency: String,
    val defaultReminderLeadDays: Int,
    val defaultReminderTime: LocalTime,
)

private val DEFAULT_STATE = SettingsUiState(
    appearance = AppearanceSettings.Default,
    activeCurrency = "USD",
    defaultReminderLeadDays = 3,
    defaultReminderTime = LocalTime(9, 0),
)

private const val MIN_LEAD_DAYS = 1
private const val MAX_LEAD_DAYS = 14

/** Every setter writes straight through to [SettingsRepository] (DataStore) — [AppTheme] and every screen observe the same flow, so a change is visible everywhere in one frame (IMPLEMENTATION_PLAN.md 2.7). */
public class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    public val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.appearanceSettings,
        settingsRepository.activeCurrency,
        settingsRepository.defaultReminderLeadDays,
        settingsRepository.defaultReminderTime,
    ) { appearance, currency, leadDays, time -> SettingsUiState(appearance, currency, leadDays, time) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_STATE)

    public fun setDarkMode(darkMode: Boolean?) {
        viewModelScope.launch { settingsRepository.setDarkMode(darkMode) }
    }

    public fun setHighContrast(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setHighContrast(enabled) }
    }

    public fun setActiveCurrency(currencyCode: String) {
        viewModelScope.launch { settingsRepository.setActiveCurrency(currencyCode) }
    }

    public fun setTextSize(textSize: TextSize) {
        viewModelScope.launch { settingsRepository.setTextSize(textSize) }
    }

    public fun setLargeTap(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setLargeTap(enabled) }
    }

    public fun setBoldText(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setBoldText(enabled) }
    }

    public fun setReducedMotion(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setReducedMotion(enabled) }
    }

    public fun setColorblindLabels(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setColorblindLabels(enabled) }
    }

    public fun decrementLeadDays() {
        val next = (uiState.value.defaultReminderLeadDays - 1).coerceAtLeast(MIN_LEAD_DAYS)
        viewModelScope.launch { settingsRepository.setDefaultReminderLeadDays(next) }
    }

    public fun incrementLeadDays() {
        val next = (uiState.value.defaultReminderLeadDays + 1).coerceAtMost(MAX_LEAD_DAYS)
        viewModelScope.launch { settingsRepository.setDefaultReminderLeadDays(next) }
    }

    public fun setDefaultReminderTime(time: LocalTime) {
        viewModelScope.launch { settingsRepository.setDefaultReminderTime(time) }
    }
}
