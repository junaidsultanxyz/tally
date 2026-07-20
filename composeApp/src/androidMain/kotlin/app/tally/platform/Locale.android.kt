package app.tally.platform

import java.util.Locale

/** Settings.ACTION_APP_LOCALE_SETTINGS deep-link — wired in Phase 4. */
actual class LocaleSettingsNavigator {
    actual fun openAppLocaleSettings() = Unit

    actual fun currentLanguageTag(): String = Locale.getDefault().toLanguageTag()
}
