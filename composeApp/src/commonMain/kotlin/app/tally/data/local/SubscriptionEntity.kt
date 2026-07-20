package app.tally.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/**
 * Room storage shape for [app.tally.domain.model.Subscription]. Enums are
 * stored as their `name()` string (mapped in `Mappers.kt`); `LocalDate`/
 * `LocalTime`/`Instant` are stored via the [Converters] registered on
 * [TallyDatabase] — real typed fields here, not manually-stringified ones.
 */
@Entity(tableName = "subscriptions")
public data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val amountMinor: Long,
    val currencyCode: String,
    val billingCycle: String,
    val firstBillingDate: LocalDate,
    val nextBillingDate: LocalDate,
    val status: String,
    val reminderEnabled: Boolean,
    val reminderLeadDays: Int,
    val reminderTime: LocalTime,
    val updatedAt: Instant,
    val deletedAt: Instant?,
)
