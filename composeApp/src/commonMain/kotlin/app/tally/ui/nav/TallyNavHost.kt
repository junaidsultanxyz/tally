package app.tally.ui.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.tally.ui.screens.AddScreen
import app.tally.ui.screens.AlertsScreen
import app.tally.ui.screens.DashboardScreen
import app.tally.ui.screens.EditSheet
import app.tally.ui.screens.SettingsScreen
import app.tally.ui.screens.UpcomingScreen
import app.tally.ui.theme.AppTheme

/**
 * Five-screen nav host + sticky bottom bar (AGENTS.md §3). Tab taps use
 * `launchSingleTop` and skip state restoration so each destination's scroll
 * position resets to top on every switch, per the Phase 0.5 spec — this is a
 * deliberate choice, not the more common "preserve each tab's scroll state"
 * bottom-nav pattern.
 *
 * [currentRoute] is tracked directly (set on every call to [navigate]) rather
 * than derived from `NavBackStackEntry.destination.hasRoute<T>()` — every
 * navigation in this app goes through [navigate] below, so we always know the
 * target already, and this sidesteps a reified-generic overload of `hasRoute`
 * that didn't resolve against the pinned Navigation Compose version.
 *
 * [editingSubscriptionId] is hoisted here (rather than per-screen) since the
 * Edit bottom sheet (Phase 2.5) is reachable from both Dashboard and Upcoming
 * rows and overlays whichever screen is current, not a nav destination itself.
 */
@Composable
internal fun TallyNavHost() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf<Route>(Route.Dashboard) }
    var editingSubscriptionId by remember { mutableStateOf<String?>(null) }
    // Tab order (left→right, matching TallyBottomBar) drives slide direction: moving to a
    // tab further right slides content in from the end, further left slides in from the
    // start — read by the transition lambdas below at the moment each navigation fires.
    var forward by remember { mutableStateOf(true) }
    val slideDurationMs = AppTheme.motion.durationMedium

    Scaffold(
        containerColor = AppTheme.colors.bgApp,
        contentColor = AppTheme.colors.textPrimary,
        bottomBar = {
            TallyBottomBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    forward = routeIndex(route) >= routeIndex(currentRoute)
                    navigateTo(navController, currentRoute, route) { currentRoute = it }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Dashboard,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            enterTransition = {
                slideInHorizontally(tween(slideDurationMs)) { fullWidth -> if (forward) fullWidth else -fullWidth } +
                    fadeIn(tween(slideDurationMs))
            },
            exitTransition = {
                slideOutHorizontally(tween(slideDurationMs)) { fullWidth -> if (forward) -fullWidth else fullWidth } +
                    fadeOut(tween(slideDurationMs))
            },
            popEnterTransition = {
                slideInHorizontally(tween(slideDurationMs)) { fullWidth -> if (forward) fullWidth else -fullWidth } +
                    fadeIn(tween(slideDurationMs))
            },
            popExitTransition = {
                slideOutHorizontally(tween(slideDurationMs)) { fullWidth -> if (forward) -fullWidth else fullWidth } +
                    fadeOut(tween(slideDurationMs))
            },
        ) {
            composable<Route.Dashboard> {
                DashboardScreen(
                    onNavigateToAlerts = {
                        forward = routeIndex(Route.Alerts) >= routeIndex(currentRoute)
                        navigateTo(navController, currentRoute, Route.Alerts) { currentRoute = it }
                    },
                    onOpenSubscription = { id -> editingSubscriptionId = id },
                )
            }
            composable<Route.Upcoming> {
                UpcomingScreen(onOpenSubscription = { id -> editingSubscriptionId = id })
            }
            composable<Route.Add> {
                AddScreen(
                    onClose = {
                        forward = routeIndex(Route.Dashboard) >= routeIndex(currentRoute)
                        navigateTo(navController, currentRoute, Route.Dashboard) { currentRoute = it }
                    },
                    onSubmitted = {
                        forward = routeIndex(Route.Dashboard) >= routeIndex(currentRoute)
                        navigateTo(navController, currentRoute, Route.Dashboard) { currentRoute = it }
                    },
                )
            }
            composable<Route.Alerts> { AlertsScreen() }
            composable<Route.Settings> { SettingsScreen() }
        }
    }

    editingSubscriptionId?.let { id ->
        EditSheet(subscriptionId = id, onDismiss = { editingSubscriptionId = null })
    }
}

/** Left→right tab order (TallyBottomBar) — the slide direction is which side of this order [to] falls on relative to the current route. */
private fun routeIndex(route: Route): Int = when (route) {
    Route.Dashboard -> 0
    Route.Upcoming -> 1
    Route.Add -> 2
    Route.Alerts -> 3
    Route.Settings -> 4
}

private fun navigateTo(navController: NavHostController, from: Route, to: Route, onNavigated: (Route) -> Unit) {
    if (to == from) return
    navController.navigate(to) {
        launchSingleTop = true
        restoreState = false
        popUpTo(Route.Dashboard) { saveState = false }
    }
    onNavigated(to)
}
