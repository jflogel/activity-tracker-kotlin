package activity.tracker.utilities

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields


fun LocalDateTime.toEpoch(): Long {
    val chicago = zoneId()
    return ZonedDateTime.of(this, chicago).toEpochSecond()
}

fun zoneId() = ZoneId.of("America/Chicago")

fun LocalDateTime.startOfDay(): LocalDateTime {
    return this.truncatedTo(ChronoUnit.DAYS)
}

fun firstDayOfYear() = LocalDate.now(zoneId()).minusWeeks(1).with(TemporalAdjusters.firstDayOfYear())
fun firstDayOfMonth() = LocalDate.now(zoneId()).minusWeeks(1).with(TemporalAdjusters.firstDayOfMonth())
fun firstDayOfWeek() = LocalDate.now(zoneId()).minusWeeks(1).with(WeekFields.SUNDAY_START.firstDayOfWeek)
fun weekOfYear() = LocalDate.now(zoneId()).dayOfYear / 7f