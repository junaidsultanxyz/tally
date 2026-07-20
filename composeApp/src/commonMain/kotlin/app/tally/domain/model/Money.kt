package app.tally.domain.model

/**
 * Integer minor units + ISO 4217 code (AGENTS.md §4/§8/§11) — **never** `Float`/`Double`
 * anywhere in the money path. `amountMinor` is the smallest unit of the currency
 * (cents for USD, whole yen for JPY, ...); [app.tally.money.Currency] knows the
 * fraction-digit count for formatting.
 */
public data class Money(val amountMinor: Long, val currencyCode: String) {
    /** Requires the same currency — money in different currencies isn't fungible without an FX rate (AGENTS.md §11 Q5). */
    public operator fun plus(other: Money): Money {
        require(currencyCode == other.currencyCode) {
            "Cannot add $currencyCode to ${other.currencyCode} without an FX rate"
        }
        return copy(amountMinor = amountMinor + other.amountMinor)
    }

    /** Integer multiply, e.g. monthly × 12 → annual. */
    public operator fun times(factor: Long): Money = copy(amountMinor = amountMinor * factor)

    /**
     * Integer divide with half-up rounding on minor units — e.g. yearly ÷ 12 for
     * cadence normalization (AGENTS.md §11). Round **once**, at the point of
     * division, never accumulate fractional remainders across calls. Prices are
     * never negative in this domain, so this only needs to handle `amountMinor >= 0`.
     */
    public fun divInto(divisor: Long): Money {
        require(divisor > 0) { "divisor must be positive" }
        require(amountMinor >= 0) { "divInto is only defined for non-negative amounts" }
        return copy(amountMinor = (amountMinor + divisor / 2) / divisor)
    }
}
