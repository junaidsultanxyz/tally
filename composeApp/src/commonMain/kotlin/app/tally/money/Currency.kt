package app.tally.money

/** One ISO 4217 currency's display symbol and minor-unit fraction-digit count. */
public data class Currency(val code: String, val symbol: String, val decimalDigits: Int)

/**
 * The kit's original 29-currency table (`ui_kits/tally-app/index.html`'s `CURRENCIES`
 * array), plus native-only additions beyond it. This [table] is the single source of truth
 * app-wide — the Settings currency picker, [app.tally.money.formatMoney], and
 * [app.tally.money.parseMoney] all read through [all]/[get], nothing else hardcodes the
 * currency set. Adding or removing an ISO 4217 entry is a one-line change here and nowhere
 * else; `CurrencyTableTest` guards against a pasted-in duplicate code or an empty symbol
 * going unnoticed.
 */
public object Currencies {
    private val table: List<Currency> = listOf(
        Currency("USD", "$", 2),
        Currency("EUR", "€", 2),
        Currency("GBP", "£", 2),
        Currency("JPY", "¥", 0),
        Currency("CNY", "¥", 2),
        Currency("INR", "₹", 2),
        Currency("PKR", "Rs ", 2), // Native addition, not in the kit's original 29.
        Currency("CAD", "C$", 2),
        Currency("AUD", "A$", 2),
        Currency("CHF", "CHF ", 2),
        Currency("BRL", "R$", 2),
        Currency("MXN", "$", 2),
        Currency("ZAR", "R ", 2),
        Currency("SGD", "S$", 2),
        Currency("HKD", "HK$", 2),
        Currency("NZD", "NZ$", 2),
        Currency("SEK", "kr ", 2),
        Currency("NOK", "kr ", 2),
        Currency("DKK", "kr ", 2),
        Currency("KRW", "₩", 0),
        Currency("RUB", "₽", 2),
        Currency("TRY", "₺", 2),
        Currency("AED", "AED ", 2),
        Currency("SAR", "SAR ", 2),
        Currency("PLN", "zł ", 2),
        Currency("THB", "฿", 2),
        Currency("IDR", "Rp ", 0),
        Currency("PHP", "₱", 2),
        Currency("MYR", "RM", 2),
        Currency("NGN", "₦", 2),
    )

    private val byCode: Map<String, Currency> = table.associateBy { it.code }

    public val all: List<Currency> = table

    public fun get(code: String): Currency =
        byCode[code] ?: error("Unknown currency code: $code")
}

internal fun pow10(n: Int): Long {
    var result = 1L
    repeat(n) { result *= 10 }
    return result
}
