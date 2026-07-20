package app.tally.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.repository.seedSubscriptions
import app.tally.data.settings.AppearanceSettings
import app.tally.data.settings.SettingsRepository
import app.tally.ui.nav.TallyNavHost
import app.tally.ui.theme.AppTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import org.koin.compose.koinInject

/** [debugBuild] seeds dev fixture data on first launch — pass `BuildConfig.DEBUG` from each platform's entry point. */
@Composable
fun TallyApp(debugBuild: Boolean = false) {
    if (debugBuild) {
        val repository = koinInject<SubscriptionRepository>()
        LaunchedEffect(Unit) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            repository.seedIfEmpty(seedSubscriptions(today))
        }
    }

    val settingsRepository = koinInject<SettingsRepository>()
    val appearanceSettings by settingsRepository.appearanceSettings.collectAsState(AppearanceSettings.Default)

    AppTheme(settings = appearanceSettings) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = AppTheme.colors.bgApp,
        ) {
            TallyNavHost()
        }
    }
}
