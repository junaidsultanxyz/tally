package app.tally.platform

import kotlinx.datetime.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

private val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

actual fun formatAbsoluteDate(date: LocalDate): String {
    @Suppress("DEPRECATION")
    val javaDate = java.time.LocalDate.of(date.year, date.monthNumber, date.day)
    return javaDate.format(formatter)
}
