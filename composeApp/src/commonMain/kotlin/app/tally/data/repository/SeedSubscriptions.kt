package app.tally.data.repository

import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Clock

/**
 * The kit's nine seed subscriptions (`ui_kits/tally-app/index.html`), with
 * `nextBillingDate` computed relative to [today] instead of the kit's
 * free-text `due` strings — **debug builds only**, and only called by the
 * app when explicitly opted into (see `SubscriptionRepository.seedIfEmpty`,
 * which additionally refuses to run if the table already has data).
 */
public fun seedSubscriptions(today: LocalDate): List<Subscription> {
    fun sub(
        name: String,
        category: Category,
        amountMinor: Long,
        daysUntilDue: Int,
        status: Status,
    ): Subscription {
        val nextBillingDate = today.plusDays(daysUntilDue)
        return Subscription(
            id = "seed-$name",
            name = name,
            category = category,
            amountMinor = amountMinor,
            currencyCode = "USD",
            billingCycle = BillingCycle.MONTHLY,
            firstBillingDate = nextBillingDate,
            nextBillingDate = nextBillingDate,
            status = status,
            reminderTime = LocalTime(9, 0),
            updatedAt = Clock.System.now(),
        )
    }

    return listOf(
        sub("Netflix", Category.ENTERTAINMENT, 1549, 3, Status.DUE),
        sub("Spotify", Category.MUSIC, 1199, 5, Status.DUE),
        sub("iCloud+", Category.STORAGE, 299, 6, Status.DUE),
        sub("Notion", Category.PRODUCTIVITY, 800, 12, Status.ACTIVE),
        sub("Disney+", Category.ENTERTAINMENT, 1399, 14, Status.ACTIVE),
        // 30 is just a placeholder "whenever it would renew if unpaused" — daysUntil()
        // ignores this field entirely for paused subs and always returns the 99 sentinel.
        sub("Peloton", Category.FITNESS, 2400, 30, Status.PAUSED),
        sub("NYT", Category.NEWS, 425, 21, Status.ACTIVE),
        sub("YouTube Premium", Category.ENTERTAINMENT, 1399, 23, Status.ACTIVE),
        sub("ChatGPT", Category.PRODUCTIVITY, 2000, 2, Status.TRIAL),
    )
}

private fun LocalDate.plusDays(days: Int): LocalDate = LocalDate.fromEpochDays(this.toEpochDays() + days)
