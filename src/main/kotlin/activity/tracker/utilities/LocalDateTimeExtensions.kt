package activity.tracker.utilities

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.*


fun LocalDateTime.toEpoch(): Long {
    val chicago = zoneId()
    return ZonedDateTime.of(this, chicago).toEpochSecond()
}

fun zoneId() = ZoneId.of("America/Chicago")

fun LocalDateTime.startOfDay(): LocalDateTime {
    return this.truncatedTo(ChronoUnit.DAYS)
}

fun firstDayOfYear() = LocalDate.now(zoneId()).with(TemporalAdjusters.firstDayOfYear())
fun firstDayOfWeek() = LocalDate.now(zoneId()).with(WeekFields.of(Locale.US).dayOfWeek(), 1)
fun weekOfYear() = LocalDate.now(zoneId()).dayOfYear / 7f