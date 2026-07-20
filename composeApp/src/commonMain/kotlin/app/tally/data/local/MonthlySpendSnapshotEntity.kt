package app.tally.data.local

import androidx.room.Entity
import kotlin.time.Instant

/**
 * One row per calendar month (`yearMonth` = `"2026-01"`), continuously
 * upserted with the live total while that month is current — by the time the
 * next month starts, this row is a reasonably fresh record of what last
 * month's spend was (IMPLEMENTATION_PLAN.md Phase 2.2 / Q1/A1). Per-item
 * breakdown lives in [MonthlySpendSnapshotItemEntity] since the "vs last
 * month" card's cause caption needs the single biggest per-subscription
 * delta, not just the aggregate.
 */
@Entity(tableName = "monthly_spend_snapshots", primaryKeys = ["yearMonth"])
public data class MonthlySpendSnapshotEntity(
    val yearMonth: String,
    val monthlyTotalMinor: Long,
    val activeCount: Int,
    val currencyCode: String,
    val capturedAt: Instant,
)

@Entity(tableName = "monthly_spend_snapshot_items", primaryKeys = ["yearMonth", "subscriptionId"])
public data class MonthlySpendSnapshotItemEntity(
    val yearMonth: String,
    val subscriptionId: String,
    val subscriptionName: String,
    val monthlyEquivalentMinor: Long,
)
