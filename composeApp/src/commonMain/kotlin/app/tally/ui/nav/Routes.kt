package app.tally.ui.nav

import kotlinx.serialization.Serializable

/**
 * Type-safe Compose Navigation routes (kotlinx.serialization-backed, Navigation
 * Compose 2.8+ style) for the five screens (AGENTS.md §3). `Add` and the Edit
 * bottom sheet are separate concerns — Edit is a modal reached from a row, not
 * a bottom-bar destination.
 */
@Serializable
internal sealed interface Route {
    @Serializable
    data object Dashboard : Route

    @Serializable
    data object Upcoming : Route

    @Serializable
    data object Add : Route

    @Serializable
    data object Alerts : Route

    @Serializable
    data object Settings : Route
}
