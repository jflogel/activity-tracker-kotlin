package activity.tracker.utilities

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun LocalDateTime.toEpoch(): Long {
    val chicago = zoneId()
    return ZonedDateTime.of(this, chicago).toEpochSecond()
}

fun zoneId() = ZoneId.of("America/Chicago")

fun LocalDateTime.startOfDay(): LocalDateTime {
    return this.truncatedTo(ChronoUnit.DAYS)
}