package app.tally.money

/**
 * `amountMinor` → display string, e.g. `1549` USD → `"$15.49"`, `1500` JPY →
 * `"¥1,500"`. Zero-decimal currencies render no cents; two-decimal currencies
 * always render exactly two digits (never truncate a trailing zero away).
 * Pure integer arithmetic throughout — no `Float`/`Double` (AGENTS.md §4).
 */
public fun formatMoney(amountMinor: Long, currencyCode: String): String {
    val currency = Currencies.get(currencyCode)
    val negative = amountMinor < 0
    val absAmount = if (negative) -amountMinor else amountMinor

    val divisor = pow10(currency.decimalDigits)
    val whole = absAmount / divisor
    val groupedWhole = groupThousands(whole)

    val amountString = if (currency.decimalDigits == 0) {
        groupedWhole
    } else {
        val fraction = (absAmount % divisor).toString().padStart(currency.decimalDigits, '0')
        "$groupedWhole.$fraction"
    }

    return (if (negative) "-" else "") + currency.symbol + amountString
}

/**
 * `amountMinor` → plain decimal string with no currency symbol or thousands grouping, e.g.
 * `1549` USD → `"15.49"`, `300` JPY → `"300"` — seeds an editable price field (Edit sheet)
 * so it round-trips cleanly back through [parseMoney].
 */
public fun moneyInputString(amountMinor: Long, currencyCode: String): String {
    val currency = Currencies.get(currencyCode)
    val divisor = pow10(currency.decimalDigits)
    val whole = (amountMinor / divisor).toString()
    return if (currency.decimalDigits == 0) {
        whole
    } else {
        val fraction = (amountMinor % divisor).toString().padStart(currency.decimalDigits, '0')
        "$whole.$fraction"
    }
}

private fun groupThousands(value: Long): String {
    val digits = value.toString()
    val sb = StringBuilder()
    for ((index, char) in digits.reversed().withIndex()) {
        if (index != 0 && index % 3 == 0) sb.append(',')
        sb.append(char)
    }
    return sb.reverse().toString()
}
