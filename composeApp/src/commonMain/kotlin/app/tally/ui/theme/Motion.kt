package app.tally.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

/** True when the OS-level reduced-motion setting is on (Android `ANIMATOR_DURATION_SCALE == 0`, iOS `UIAccessibility.isReduceMotionEnabled`). */
@Composable
internal expect fun isSystemReducedMotionEnabled(): Boolean

/**
 * Durations every animation in the app must read from — never hardcode a
 * duration. Collapses to 0ms when motion is disabled, whether that's the
 * user's in-app "Reduce motion" toggle or the OS setting (the two are ORed
 * together when this is built, see [AppTheme]).
 */
internal data class TallyMotion(val enabled: Boolean) {
    val durationShort: Int get() = if (enabled) 150 else 0
    val durationMedium: Int get() = if (enabled) 300 else 0
    val durationLong: Int get() = if (enabled) 450 else 0
}

internal val LocalMotion = staticCompositionLocalOf { TallyMotion(enabled = true) }
