package app.tally.di

import org.koin.core.module.Module

/**
 * expect/actual bindings for Notifier, AuthProvider, SecureStore, etc.
 * Each platform source set supplies its own [platformModule] with the real
 * androidMain/iosMain actuals (Phase 3/5); this common declaration is the
 * shared entry point [initKoin] wires in.
 */
expect val platformModule: Module
