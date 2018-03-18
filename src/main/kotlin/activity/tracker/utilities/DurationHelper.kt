package activity.tracker.utilities

import activity.tracker.db.model.Measurement

fun convertToMinutes(measurement: Measurement): Float {
    return when(measurement.unit) {
        "minutes" -> measurement.value
        "seconds" -> measurement.value / 60
        else -> 0F
    }
}