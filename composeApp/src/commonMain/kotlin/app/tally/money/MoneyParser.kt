package app.tally.money

/**
 * User input string → `amountMinor`. Accepts either `.` or `,` as the decimal
 * separator; anything else non-numeric is stripped. Empty or unparseable
 * input coerces to `0` (spec §14). Pure integer arithmetic — no `Float`/`Double`.
 *
 * The last `.`/`,` in the string is only treated as a **decimal** separator
 * if what follows it has **at most** [Currency.decimalDigits] digits for
 * [currencyCode] — otherwise every `.`/`,` is treated as grouping
 * punctuation. This currency-aware check is necessary: naively picking
 * "whichever separator appears last" misreads `"1,500"` for JPY (0 decimal
 * digits) as `1` + a discarded `.500` fraction instead of `1500` — a real
 * bug caught by this file's own unit tests before this rule existed.
 *
 * "At most", not "exactly" [Currency.decimalDigits] — an earlier version
 * required an exact match, which misread a genuinely partial-precision
 * decimal like `"3.5"` for USD (1 trailing digit against a 2-digit
 * currency) as grouping punctuation, silently turning it into `$35.00`
 * instead of `$3.50`. Requiring *at most* still correctly rejects
 * `"1,500"` as a decimal for USD (3 trailing digits exceeds 2), so the
 * original JPY/thousands-grouping behavior this rule exists for is
 * unaffected — only inputs with fewer digits than the currency allows
 * (a real, common case) now parse correctly.
 */
public fun parseMoney(input: String, currencyCode: String): Long {
    val currency = Currencies.get(currencyCode)
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return 0L

    val cleaned = buildString {
        for (c in trimmed) {
            if (c.isDigit() || c == '.' || c == ',' || c == '-') append(c)
        }
    }
    if (cleaned.isEmpty() || cleaned == "-") return 0L

    val negative = cleaned.startsWith("-")
    val unsigned = cleaned.removePrefix("-")
    val decimalDigits = currency.decimalDigits

    val lastSeparatorIndex = maxOf(unsigned.lastIndexOf('.'), unsigned.lastIndexOf(','))
    val integerDigits: String
    val fractionDigitsRaw: String
    val looksLikeDecimal = lastSeparatorIndex != -1 &&
        decimalDigits > 0 &&
        unsigned.substring(lastSeparatorIndex + 1).filter { it.isDigit() }.length <= decimalDigits

    if (looksLikeDecimal) {
        integerDigits = unsigned.substring(0, lastSeparatorIndex).filter { it.isDigit() }
        fractionDigitsRaw = unsigned.substring(lastSeparatorIndex + 1).filter { it.isDigit() }
    } else {
        integerDigits = unsigned.filter { it.isDigit() }
        fractionDigitsRaw = ""
    }

    val integerValue = integerDigits.ifEmpty { "0" }.toLongOrNull() ?: return 0L
    val fractionValue = if (decimalDigits == 0) {
        0L
    } else {
        fractionDigitsRaw.padEnd(decimalDigits, '0').take(decimalDigits).ifEmpty { "0" }.toLongOrNull() ?: 0L
    }

    val minor = integerValue * pow10(decimalDigits) + fractionValue
    return if (negative) -minor else minor
}
