package app.tally.data.local

import androidx.room.Entity
import kotlin.time.Instant

/**
 * One cached FX rate pair, e.g. (`USD`, `PKR`, `278.5`) meaning 1 USD = 278.5 PKR. A whole
 * fetch (all target currencies for one base) shares the same [fetchedAt], including a
 * `baseCurrency == targetCurrency, rate = 1.0` row for the base itself — that row doubles as
 * the freshness marker `ExchangeRateRepository` checks before deciding whether to re-fetch.
 */
@Entity(tableName = "exchange_rate", primaryKeys = ["baseCurrency", "targetCurrency"])
public data class ExchangeRateEntity(
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val fetchedAt: Instant,
)
