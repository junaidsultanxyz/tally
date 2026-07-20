package app.tally.data.settings

/** Maps to `--type-scale` in `tokens/typography.css` / `[data-text-size]` in `accessibility.css`. */
public enum class TextSize(public val scale: Float) {
    NORMAL(1.0f),
    LARGE(1.15f),
    X_LARGE(1.30f),
    XX_LARGE(1.50f),
}

/**
 * The native equivalent of the design system's `data-theme` / `data-contrast`
 * / `data-text-size` / `data-tap` / `data-motion` attributes (AGENTS.md §15).
 * Backed by DataStore via `SettingsRepository` (Phase 1.3); this is just the
 * value shape — [AppTheme] only needs an immutable snapshot to render from.
 */
public data class AppearanceSettings(
    val darkMode: Boolean?, // null = follow system
    val highContrast: Boolean = false,
    val textSize: TextSize = TextSize.NORMAL,
    val largeTap: Boolean = false,
    val boldText: Boolean = false,
    val reducedMotion: Boolean = false,
    val colorblindLabels: Boolean = true,
) {
    public companion object {
        public val Default: AppearanceSettings = AppearanceSettings(darkMode = null)
    }
}
