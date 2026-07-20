package app.tally.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

// version 3: added exchange_rate (currency-conversion cache). No migration written —
// destructive fallback is fine pre-release (DatabaseBuilder.kt), there's no shipped user data yet.
@Database(
    entities = [
        SubscriptionEntity::class,
        MonthlySpendSnapshotEntity::class,
        MonthlySpendSnapshotItemEntity::class,
        ExchangeRateEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(Converters::class)
@ConstructedBy(TallyDatabaseConstructor::class)
public abstract class TallyDatabase : RoomDatabase() {
    public abstract fun subscriptionDao(): SubscriptionDao
    public abstract fun monthlySpendSnapshotDao(): MonthlySpendSnapshotDao
    public abstract fun exchangeRateDao(): ExchangeRateDao
}

/** KSP generates the actual `Impl` class; each platform's actual wires it up (Room KMP's required multiplatform construction pattern). */
@Suppress("NO_ACTUAL_FOR_EXPECT")
public expect object TallyDatabaseConstructor : RoomDatabaseConstructor<TallyDatabase> {
    override fun initialize(): TallyDatabase
}
