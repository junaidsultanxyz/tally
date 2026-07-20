package app.tally.platform

import platform.Foundation.NSLocale
import platform.Foundation.preferredLanguages

/** UIApplication.openSettingsURLString deep-link — wired in Phase 4. */
actual class LocaleSettingsNavigator {
    actual fun openAppLocaleSettings() = Unit

    actual fun currentLanguageTag(): String =
        NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
}
