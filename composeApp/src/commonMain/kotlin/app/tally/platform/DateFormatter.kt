package app.tally.platform

import kotlinx.datetime.LocalDate

/**
 * A real locale-aware "medium" absolute date (e.g. "Jul 23, 2026" in `en-US`, formatted per
 * the device's actual locale elsewhere) — used where a date appears inside a natural-language
 * sentence (Alerts row subline, FUNCTIONALITIES.md §7: "absolute date localized, not
 * hardcoded"). Each platform's own date formatter is authoritative; this file never hardcodes
 * an English month name.
 */
expect fun formatAbsoluteDate(date: LocalDate): String
