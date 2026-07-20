package app.tally.platform

/** Deep-link into the OS per-app language settings screen (Android 13+ App languages / iOS Settings). */
expect class LocaleSettingsNavigator {
    fun openAppLocaleSettings()
    fun currentLanguageTag(): String
}
