package app.tally.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
public interface ExchangeRateDao {
    @Upsert
    public suspend fun upsertAll(rates: List<ExchangeRateEntity>)

    @Query("SELECT * FROM exchange_rate WHERE baseCurrency = :base AND targetCurrency = :target LIMIT 1")
    public suspend fun getRate(base: String, target: String): ExchangeRateEntity?

    /** The base-currency self-row (rate = 1.0) doubles as this base's freshness marker. */
    @Query("SELECT * FROM exchange_rate WHERE baseCurrency = :base AND targetCurrency = :base LIMIT 1")
    public suspend fun getFreshnessMarker(base: String): ExchangeRateEntity?
}
