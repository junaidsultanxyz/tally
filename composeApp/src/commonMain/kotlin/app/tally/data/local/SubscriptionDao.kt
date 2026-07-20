package app.tally.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
public interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions WHERE deletedAt IS NULL")
    public fun observeAll(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id AND deletedAt IS NULL")
    public fun observeById(id: String): Flow<SubscriptionEntity?>

    @Upsert
    public suspend fun upsert(entity: SubscriptionEntity)

    @Query("UPDATE subscriptions SET deletedAt = :at WHERE id = :id")
    public suspend fun softDelete(id: String, at: Instant)

    @Query("DELETE FROM subscriptions WHERE deletedAt IS NOT NULL AND deletedAt < :cutoff")
    public suspend fun hardDeleteTombstonesOlderThan(cutoff: Instant)

    /** Includes tombstones — the sync engine (Phase 5) needs to see deletes to propagate them. */
    @Query("SELECT * FROM subscriptions")
    public suspend fun getAllIncludingTombstones(): List<SubscriptionEntity>
}
