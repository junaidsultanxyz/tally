package app.tally.data.local

import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Category
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription

public fun SubscriptionEntity.toDomain(): Subscription = Subscription(
    id = id,
    name = name,
    category = Category.valueOf(category),
    amountMinor = amountMinor,
    currencyCode = currencyCode,
    billingCycle = BillingCycle.valueOf(billingCycle),
    firstBillingDate = firstBillingDate,
    nextBillingDate = nextBillingDate,
    status = Status.valueOf(status),
    reminderEnabled = reminderEnabled,
    reminderLeadDays = reminderLeadDays,
    reminderTime = reminderTime,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)

public fun Subscription.toEntity(): SubscriptionEntity = SubscriptionEntity(
    id = id,
    name = name,
    category = category.name,
    amountMinor = amountMinor,
    currencyCode = currencyCode,
    billingCycle = billingCycle.name,
    firstBillingDate = firstBillingDate,
    nextBillingDate = nextBillingDate,
    status = status.name,
    reminderEnabled = reminderEnabled,
    reminderLeadDays = reminderLeadDays,
    reminderTime = reminderTime,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)
