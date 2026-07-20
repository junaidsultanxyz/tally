package app.tally.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
public interface MonthlySpendSnapshotDao {
    @Upsert
    public suspend fun upsertSnapshot(snapshot: MonthlySpendSnapshotEntity)

    @Query("DELETE FROM monthly_spend_snapshot_items WHERE yearMonth = :yearMonth")
    public suspend fun clearItems(yearMonth: String)

    @Upsert
    public suspend fun upsertItems(items: List<MonthlySpendSnapshotItemEntity>)

    @Query("SELECT * FROM monthly_spend_snapshots WHERE yearMonth = :yearMonth")
    public suspend fun getSnapshot(yearMonth: String): MonthlySpendSnapshotEntity?

    @Query("SELECT * FROM monthly_spend_snapshot_items WHERE yearMonth = :yearMonth")
    public suspend fun getItems(yearMonth: String): List<MonthlySpendSnapshotItemEntity>
}
