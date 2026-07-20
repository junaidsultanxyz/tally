package app.tally.domain.usecase

import app.tally.domain.model.BillingCycle
import app.tally.domain.model.Status
import app.tally.domain.model.Subscription
import app.tally.money.Currencies
import app.tally.money.pow10
import kotlinx.datetime.LocalDate

public data class ComputeTotalsResult(
    /** Cadence-normalized monthly total, in the active currency's minor units — MONTHLY as-is, YEARLY ÷12, WEEKLY ×52÷12, QUARTERLY ÷3 (AGENTS.md §11). */
    val monthlyTotalMinor: Long,
    val annualTotalMinor: Long,
    /** Non-paused subscription count, across all currencies — this is a count, not a monetary figure. */
    val activeCount: Int,
    /** Non-paused && `daysUntil <= 7`. */
    val upcomingCount: Int,
    /** `daysUntil <= 6`, sorted soonest-first — the Dashboard's "Renewing soon" list. */
    val renewingSoon: List<Subscription>,
    /** Monthly total per currency present, for a future multi-currency breakdown UI (Q5/A5 restricts the primary totals above to [activeCurrency] for v1, but this stays open). */
    val perCurrencyBreakdown: Map<String, Long>,
)

/**
 * The single source for every derived dashboard value (AGENTS.md §11). Scaled
 * integer accumulation, never per-item rounding: each subscription's
 * monthly-equivalent contribution is kept as an **exact** scaled integer
 * (× the LCM of the cadence divisors involved) and summed that way; rounding
 * to real minor units happens exactly once, on the final sum, per currency —
 * this is what the spec means by "round once at the end, not per-item, to
 * avoid drift" (three subs each with a true 0.5-cent monthly-equivalent must
 * sum to 2 cents overall, not 3, which is what per-item rounding would give).
 *
 * [ratesToActiveCurrency] (`sourceCurrencyCode -> units of activeCurrency per 1 unit of
 * source`) lets [monthlyTotalMinor]/[annualTotalMinor] include subscriptions billed in a
 * different currency, converted in — a real FX rate is unavoidably a `Double` (it isn't a
 * ratio of small integers), but the running sum stays in `Double` right up to one single
 * [roundHalfUpDouble] call at the very end, so a converted item still only gets rounded once,
 * same discipline as the same-currency path. A currency missing from the map (offline with
 * nothing cached yet, e.g.) is simply excluded from the total rather than guessed at —
 * matches the pre-conversion behavior for any non-active currency. [perCurrencyBreakdown]
 * stays native-currency and unconverted regardless, for its own future per-currency UI.
 *
 * The rate alone isn't enough to convert *minor* units correctly when the two currencies
 * don't share the same decimal-digit count (e.g. JPY has 0, USD has 2) — `amountMinor * rate`
 * would be off by a factor of 100 for a JPY→USD conversion. Each converted item is additionally
 * scaled by `10^(targetDecimalDigits - sourceDecimalDigits)` to correct for that (caught by
 * this file's own JPY→USD unit test before shipping — same-decimal-digit pairs like EUR/USD
 * happened to hide the bug since the adjustment factor is 1 for those).
 */
public fun computeTotals(
    subscriptions: List<Subscription>,
    activeCurrency: String,
    today: LocalDate,
    ratesToActiveCurrency: Map<String, Double> = emptyMap(),
): ComputeTotalsResult {
    val nonPaused = subscriptions.filter { it.status != Status.PAUSED }

    val activeCount = nonPaused.size
    val upcomingCount = nonPaused.count { daysUntil(it.nextBillingDate, it.status, today) <= 7 }
    val renewingSoon = nonPaused
        .filter { daysUntil(it.nextBillingDate, it.status, today) <= 6 }
        .sortedBy { daysUntil(it.nextBillingDate, it.status, today) }

    val scaledSumsByCurrency: Map<String, Long> = nonPaused
        .groupBy { it.currencyCode }
        .mapValues { (_, subs) -> subs.sumOf { scaledMonthlyContribution(it.amountMinor, it.billingCycle) } }

    val perCurrencyBreakdown: Map<String, Long> = scaledSumsByCurrency.mapValues { (_, scaledSum) ->
        roundHalfUp(scaledSum, SCALE)
    }

    val combinedScaledSum: Double = scaledSumsByCurrency.entries.sumOf { (currency, scaledSum) ->
        when {
            currency == activeCurrency -> scaledSum.toDouble()
            else -> {
                val rate = ratesToActiveCurrency[currency]
                if (rate == null) {
                    0.0
                } else {
                    val decimalDigitAdjustment = pow10(Currencies.get(activeCurrency).decimalDigits).toDouble() /
                        pow10(Currencies.get(currency).decimalDigits).toDouble()
                    scaledSum.toDouble() * rate * decimalDigitAdjustment
                }
            }
        }
    }
    val monthlyTotalMinor = roundHalfUpDouble(combinedScaledSum, SCALE)
    val annualTotalMinor = monthlyTotalMinor * 12

    return ComputeTotalsResult(
        monthlyTotalMinor = monthlyTotalMinor,
        annualTotalMinor = annualTotalMinor,
        activeCount = activeCount,
        upcomingCount = upcomingCount,
        renewingSoon = renewingSoon,
        perCurrencyBreakdown = perCurrencyBreakdown,
    )
}

/** LCM of the cadence divisors involved (12 for yearly, 3 for quarterly, and weekly's ×52÷12 = ×13/3). */
private const val SCALE = 12L

private fun scaledMonthlyContribution(amountMinor: Long, billingCycle: BillingCycle): Long = when (billingCycle) {
    BillingCycle.MONTHLY -> amountMinor * SCALE // amountMinor × 1, scaled
    BillingCycle.YEARLY -> amountMinor // amountMinor ÷ 12, scaled by 12 → amountMinor × 1
    BillingCycle.WEEKLY -> amountMinor * 52 // amountMinor × 52 ÷ 12, scaled by 12 → amountMinor × 52
    BillingCycle.QUARTERLY -> amountMinor * 4 // amountMinor ÷ 3, scaled by 12 → amountMinor × 4
    BillingCycle.CUSTOM -> error(
        "computeTotals: BillingCycle.CUSTOM has no defined interval yet — see AdvanceBillingDate.kt.",
    )
}

private fun roundHalfUp(scaledSum: Long, scale: Long): Long {
    require(scaledSum >= 0) { "scaledSum is expected to be non-negative (prices are never negative)" }
    return (scaledSum + scale / 2) / scale
}

/** [roundHalfUp]'s Double counterpart — for an exact-integer-valued input this agrees with it bit-for-bit (Doubles represent every Long up to 2^53 exactly, far beyond any realistic total here), so the no-conversion path is unaffected. */
private fun roundHalfUpDouble(scaledSum: Double, scale: Long): Long {
    require(scaledSum >= 0.0) { "scaledSum is expected to be non-negative (prices are never negative)" }
    return kotlin.math.floor((scaledSum + scale / 2.0) / scale).toLong()
}

/**
 * One subscription's own monthly-equivalent amount, rounded in isolation —
 * for standalone display (e.g. a monthly-spend-snapshot cause caption), never
 * for summing multiple subscriptions (that must go through [computeTotals]'s
 * scaled accumulation instead, or per-item rounding drift creeps back in).
 */
public fun monthlyEquivalentMinor(amountMinor: Long, billingCycle: BillingCycle): Long = when (billingCycle) {
    BillingCycle.MONTHLY -> amountMinor
    BillingCycle.YEARLY -> roundHalfUp(amountMinor, 12)
    BillingCycle.WEEKLY -> roundHalfUp(amountMinor * 52, 12)
    BillingCycle.QUARTERLY -> roundHalfUp(amountMinor, 3)
    BillingCycle.CUSTOM -> error(
        "monthlyEquivalentMinor: BillingCycle.CUSTOM has no defined interval yet — see AdvanceBillingDate.kt.",
    )
}
