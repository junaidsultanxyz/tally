package app.tally.domain.usecase

import app.tally.data.local.MonthlySpendSnapshotDao
import app.tally.data.local.MonthlySpendSnapshotItemEntity
import app.tally.data.local.MonthlySpendSnapshotEntity
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlin.time.Clock

/** `"2026-01"` — a natural per-month dedupe key. */
public fun yearMonthKey(date: LocalDate): String =
    "${date.year}-${date.monthNumber.toString().padStart(2, '0')}"

/**
 * Upserts the **current** month's snapshot (total + per-subscription
 * breakdown) with live data. Call this on every Dashboard load — by the time
 * next month starts, this row is a reasonably fresh record of what last
 * month's spend was, without needing a background job (Phase 2 is still
 * fully offline/foreground-only).
 */
public suspend fun captureMonthlySnapshot(
    dao: MonthlySpendSnapshotDao,
    subscriptions: List<Subscription>,
    activeCurrency: String,
    today: LocalDate,
    ratesToActiveCurrency: Map<String, Double> = emptyMap(),
) {
    val totals = computeTotals(subscriptions, activeCurrency, today, ratesToActiveCurrency)
    val yearMonth = yearMonthKey(today)

    dao.upsertSnapshot(
        MonthlySpendSnapshotEntity(
            yearMonth = yearMonth,
            monthlyTotalMinor = totals.monthlyTotalMinor,
            activeCount = totals.activeCount,
            currencyCode = activeCurrency,
            capturedAt = Clock.System.now(),
        ),
    )
    dao.clearItems(yearMonth)
    dao.upsertItems(
        subscriptions
            .filter { it.status != Status.PAUSED && it.currencyCode == activeCurrency }
            .map {
                MonthlySpendSnapshotItemEntity(
                    yearMonth = yearMonth,
                    subscriptionId = it.id,
                    subscriptionName = it.name,
                    monthlyEquivalentMinor = monthlyEquivalentMinor(it.amountMinor, it.billingCycle),
                )
            },
    )
}

/** Not a localized string — [causeSubscriptionName] pairs with this in the UI layer to build "%s price rise" / "%s price drop" via string resources. */
public sealed interface MonthlyDeltaDirection {
    public data object PriceRise : MonthlyDeltaDirection
    public data object PriceDrop : MonthlyDeltaDirection
}

public data class MonthlyDeltaCause(val direction: MonthlyDeltaDirection, val subscriptionName: String)

public data class MonthlyDelta(val deltaMinor: Long, val cause: MonthlyDeltaCause?)

/**
 * `null` when there's no snapshot for the previous month yet — the "vs last
 * month" card is hidden entirely in that case (Q1/A1), not shown with a
 * fabricated zero.
 */
public suspend fun computeMonthlyDelta(
    dao: MonthlySpendSnapshotDao,
    currentLiveTotalMinor: Long,
    currentSubscriptions: List<Subscription>,
    activeCurrency: String,
    today: LocalDate,
): MonthlyDelta? {
    val previousYearMonth = yearMonthKey(today.minus(DatePeriod(months = 1)))
    val previousSnapshot = dao.getSnapshot(previousYearMonth) ?: return null
    if (previousSnapshot.currencyCode != activeCurrency) return null

    val deltaMinor = currentLiveTotalMinor - previousSnapshot.monthlyTotalMinor

    val previousItemsById = dao.getItems(previousYearMonth).associateBy { it.subscriptionId }
    val currentItemsById = currentSubscriptions
        .filter { it.status != Status.PAUSED && it.currencyCode == activeCurrency }
        .associate { it.id to monthlyEquivalentMinor(it.amountMinor, it.billingCycle) }

    var cause: MonthlyDeltaCause? = null
    var biggestAbsDelta = 0L
    for (id in previousItemsById.keys + currentItemsById.keys) {
        val before = previousItemsById[id]?.monthlyEquivalentMinor ?: 0L
        val after = currentItemsById[id] ?: 0L
        val itemDelta = after - before
        val absDelta = if (itemDelta < 0) -itemDelta else itemDelta
        if (absDelta > biggestAbsDelta) {
            val name = currentSubscriptions.find { it.id == id }?.name ?: previousItemsById[id]?.subscriptionName
            if (name != null) {
                biggestAbsDelta = absDelta
                cause = MonthlyDeltaCause(
                    direction = if (itemDelta > 0) MonthlyDeltaDirection.PriceRise else MonthlyDeltaDirection.PriceDrop,
                    subscriptionName = name,
                )
            }
        }
    }

    return MonthlyDelta(deltaMinor = deltaMinor, cause = cause)
}
