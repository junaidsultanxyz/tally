package app.tally.money

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Guards [Currencies]' "one-line change" claim: whoever adds or removes a row later gets
 * caught here, not by a runtime crash somewhere else, if the edit was malformed.
 */
class CurrencyTableTest {

    @Test
    fun `every code is unique`() {
        val codes = Currencies.all.map { it.code }
        assertEquals(codes.size, codes.toSet().size, "duplicate currency code(s) in the table")
    }

    @Test
    fun `every code is a 3-letter ISO 4217 code`() {
        Currencies.all.forEach { currency ->
            assertTrue(currency.code.length == 3 && currency.code.all { it.isUpperCase() }, "malformed code: ${currency.code}")
        }
    }

    @Test
    fun `every entry has a non-blank symbol and non-negative decimal count`() {
        Currencies.all.forEach { currency ->
            assertTrue(currency.symbol.isNotBlank(), "${currency.code} has a blank symbol")
            assertTrue(currency.decimalDigits >= 0, "${currency.code} has a negative decimalDigits")
        }
    }

    @Test
    fun `get resolves every code in the table`() {
        Currencies.all.forEach { currency ->
            assertEquals(currency, Currencies.get(currency.code))
        }
    }
}
