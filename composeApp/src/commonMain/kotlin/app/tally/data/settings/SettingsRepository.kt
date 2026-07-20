package app.tally.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

private object Keys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
    val TEXT_SIZE = stringPreferencesKey("text_size")
    val LARGE_TAP = booleanPreferencesKey("large_tap")
    val BOLD_TEXT = booleanPreferencesKey("bold_text")
    val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
    val COLORBLIND_LABELS = booleanPreferencesKey("colorblind_labels")
    val ACTIVE_CURRENCY = stringPreferencesKey("active_currency")
    val REMINDER_LEAD_DAYS = intPreferencesKey("reminder_lead_days")
    val REMINDER_TIME = stringPreferencesKey("reminder_time")
}

private const val DEFAULT_CURRENCY = "USD"
private const val DEFAULT_REMINDER_LEAD_DAYS = 3
private val DEFAULT_REMINDER_TIME = LocalTime(9, 0)

/**
 * DataStore-backed — the native equivalent of the kit's `data-*` attributes
 * plus its currency/reminder settings (AGENTS.md §15/§17). `darkMode: Boolean?`
 * genuinely absent from the store (not a sentinel value) means "follow system".
 */
public class SettingsRepository(private val dataStore: DataStore<Preferences>) {

    public val appearanceSettings: Flow<AppearanceSettings> = dataStore.data.map { prefs ->
        AppearanceSettings(
            darkMode = prefs[Keys.DARK_MODE],
            highContrast = prefs[Keys.HIGH_CONTRAST] ?: false,
            textSize = prefs[Keys.TEXT_SIZE]?.let { runCatching { TextSize.valueOf(it) }.getOrNull() } ?: TextSize.NORMAL,
            largeTap = prefs[Keys.LARGE_TAP] ?: false,
            boldText = prefs[Keys.BOLD_TEXT] ?: false,
            reducedMotion = prefs[Keys.REDUCED_MOTION] ?: false,
            colorblindLabels = prefs[Keys.COLORBLIND_LABELS] ?: true,
        )
    }

    public val activeCurrency: Flow<String> = dataStore.data.map { it[Keys.ACTIVE_CURRENCY] ?: DEFAULT_CURRENCY }

    public val defaultReminderLeadDays: Flow<Int> =
        dataStore.data.map { it[Keys.REMINDER_LEAD_DAYS] ?: DEFAULT_REMINDER_LEAD_DAYS }

    public val defaultReminderTime: Flow<LocalTime> = dataStore.data.map { prefs ->
        prefs[Keys.REMINDER_TIME]?.let { runCatching { LocalTime.parse(it) }.getOrNull() } ?: DEFAULT_REMINDER_TIME
    }

    public suspend fun setDarkMode(darkMode: Boolean?) {
        dataStore.edit { prefs ->
            if (darkMode == null) prefs.remove(Keys.DARK_MODE) else prefs[Keys.DARK_MODE] = darkMode
        }
    }

    public suspend fun setHighContrast(enabled: Boolean) {
        dataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }

    public suspend fun setTextSize(textSize: TextSize) {
        dataStore.edit { it[Keys.TEXT_SIZE] = textSize.name }
    }

    public suspend fun setLargeTap(enabled: Boolean) {
        dataStore.edit { it[Keys.LARGE_TAP] = enabled }
    }

    public suspend fun setBoldText(enabled: Boolean) {
        dataStore.edit { it[Keys.BOLD_TEXT] = enabled }
    }

    public suspend fun setReducedMotion(enabled: Boolean) {
        dataStore.edit { it[Keys.REDUCED_MOTION] = enabled }
    }

    public suspend fun setColorblindLabels(enabled: Boolean) {
        dataStore.edit { it[Keys.COLORBLIND_LABELS] = enabled }
    }

    public suspend fun setActiveCurrency(currencyCode: String) {
        dataStore.edit { it[Keys.ACTIVE_CURRENCY] = currencyCode }
    }

    public suspend fun setDefaultReminderLeadDays(days: Int) {
        dataStore.edit { it[Keys.REMINDER_LEAD_DAYS] = days }
    }

    public suspend fun setDefaultReminderTime(time: LocalTime) {
        dataStore.edit { it[Keys.REMINDER_TIME] = time.toString() }
    }
}
