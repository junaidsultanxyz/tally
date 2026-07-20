package app.tally.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/** `LocalDate`/`LocalTime` as ISO-8601 strings, `Instant` as epoch milliseconds — picked once, used consistently everywhere. */
public class Converters {
    @TypeConverter
    public fun localDateToString(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    public fun stringToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    public fun localTimeToString(value: LocalTime?): String? = value?.toString()

    @TypeConverter
    public fun stringToLocalTime(value: String?): LocalTime? = value?.let(LocalTime::parse)

    @TypeConverter
    public fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilliseconds()

    @TypeConverter
    public fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::fromEpochMilliseconds)
}
