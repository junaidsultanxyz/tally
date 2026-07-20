package app.tally.di

import app.tally.ui.screens.AddViewModel
import app.tally.ui.screens.AlertsViewModel
import app.tally.ui.screens.DashboardViewModel
import app.tally.ui.screens.EditViewModel
import app.tally.ui.screens.SettingsViewModel
import app.tally.ui.screens.UpcomingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/** Screen ViewModels. */
val uiModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get()) }
    viewModel { UpcomingViewModel(get(), get()) }
    viewModel { AddViewModel(get(), get()) }
    viewModel { EditViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { AlertsViewModel(get(), get()) }
}
