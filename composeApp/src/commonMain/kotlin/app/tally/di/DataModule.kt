package app.tally.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.tally.data.local.TallyDatabase
import app.tally.data.local.createTallyDatabase
import app.tally.data.remote.ExchangeRateApi
import app.tally.data.remote.ExchangeRateSource
import app.tally.data.repository.ExchangeRateRepository
import app.tally.data.repository.SubscriptionRepository
import app.tally.data.settings.SettingsRepository
import app.tally.data.settings.createSettingsDataStore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** Room, DataStore, repositories, the Drive client. */
val dataModule = module {
    single<TallyDatabase> { createTallyDatabase() }
    single { get<TallyDatabase>().subscriptionDao() }
    single { get<TallyDatabase>().monthlySpendSnapshotDao() }
    single { get<TallyDatabase>().exchangeRateDao() }
    single { SubscriptionRepository(get()) }

    single<DataStore<Preferences>> { createSettingsDataStore() }
    single { SettingsRepository(get()) }

    single {
        HttpClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
            install(HttpTimeout)
        }
    }
    single<ExchangeRateSource> { ExchangeRateApi(get()) }
    single { ExchangeRateRepository(get(), get()) }
}
