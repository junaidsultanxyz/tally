package app.tally.money

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {

    @Test
    fun `USD formats with two decimals`() {
        assertEquals("$15.49", formatMoney(1549, "USD"))
    }

    @Test
    fun `zero-decimal currencies render no cents`() {
        assertEquals("¥1,500", formatMoney(1500, "JPY"))
        assertEquals("₩1,500", formatMoney(1500, "KRW"))
        assertEquals("Rp 1,500", formatMoney(1500, "IDR"))
    }

    @Test
    fun `two-decimal currencies never truncate trailing zero`() {
        assertEquals("$1.00", formatMoney(100, "USD"))
        assertEquals("$1.50", formatMoney(150, "USD"))
    }

    @Test
    fun `grouping inserts a comma every three digits`() {
        assertEquals("$1,715.00", formatMoney(171500, "USD"))
        assertEquals("$1,234,567.89", formatMoney(123456789, "USD"))
    }

    @Test
    fun `negative amounts prefix a minus before the symbol`() {
        assertEquals("-$8.50", formatMoney(-850, "USD"))
    }

    @Test
    fun `empty input parses to zero`() {
        assertEquals(0L, parseMoney("", "USD"))
        assertEquals(0L, parseMoney("   ", "USD"))
    }

    @Test
    fun `invalid input parses to zero`() {
        assertEquals(0L, parseMoney("abc", "USD"))
        assertEquals(0L, parseMoney("-", "USD"))
    }

    @Test
    fun `parsing stays exact where float math would lose a cent`() {
        // 0.10 + 0.20 famously != 0.30 under Double math; integer minor units must not repeat that.
        val a = parseMoney("0.10", "USD")
        val b = parseMoney("0.20", "USD")
        assertEquals(30L, a + b)
    }

    @Test
    fun `parser accepts comma as decimal separator`() {
        assertEquals(1549L, parseMoney("15,49", "USD"))
    }

    @Test
    fun `parser respects zero-decimal currencies`() {
        assertEquals(1500L, parseMoney("1500", "JPY"))
        assertEquals(1500L, parseMoney("1,500", "JPY"))
    }

    @Test
    fun `parser strips grouping separators from the integer part`() {
        assertEquals(171500L, parseMoney("1,715.00", "USD"))
    }

    @Test
    fun `parser accepts partial-precision decimals below the currency's max`() {
        // Real bug: "3.5" (1 trailing digit) for a 2-decimal currency was misread as
        // grouping punctuation and stripped to "35" -> $35.00 instead of $3.50.
        assertEquals(350L, parseMoney("3.5", "USD"))
        assertEquals(350L, parseMoney("3,5", "USD"))
        assertEquals(300L, parseMoney("3.", "USD"))
    }

    @Test
    fun `parser still treats a 3-digit trailing group as grouping, not decimal, for a 2-decimal currency`() {
        assertEquals(150000L, parseMoney("1,500", "USD")) // $1,500.00 whole dollars, not $1.50 (+ a dropped "0")
    }

    @Test
    fun `divInto rounds half up on minor units`() {
        // A yearly $12.99 subscription (1299 minor units) normalized to a monthly equivalent.
        val yearly = app.tally.domain.model.Money(1299, "USD")
        assertEquals(108L, yearly.divInto(12).amountMinor) // 1299/12 = 108.25 -> 108
        val exactHalf = app.tally.domain.model.Money(6, "USD")
        assertEquals(1L, exactHalf.divInto(12).amountMinor) // 6/12 = 0.5 -> rounds up to 1
    }
}
