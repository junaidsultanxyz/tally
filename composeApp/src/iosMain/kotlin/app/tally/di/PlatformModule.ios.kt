package app.tally.di

import app.tally.platform.AuthProvider
import app.tally.platform.LocaleSettingsNavigator
import app.tally.platform.NotificationPermissionController
import app.tally.platform.Notifier
import app.tally.platform.SecureStore
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { AuthProvider() }
    single { Notifier() }
    single { NotificationPermissionController() }
    single { SecureStore() }
    single { LocaleSettingsNavigator() }
}
