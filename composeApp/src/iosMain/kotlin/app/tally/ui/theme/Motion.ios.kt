package app.tally.ui.theme

import androidx.compose.runtime.Composable
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

@Composable
internal actual fun isSystemReducedMotionEnabled(): Boolean = UIAccessibilityIsReduceMotionEnabled()
