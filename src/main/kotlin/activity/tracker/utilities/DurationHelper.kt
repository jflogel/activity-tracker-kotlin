package activity.tracker.utilities

import activity.tracker.db.model.Measurement

fun convertDuration(measurement: Measurement, unit: String): Measurement {
    val convertedValue = when(measurement.unit) {
        "minutes" -> measurement.value
        "seconds" -> measurement.value / 60
        else -> 0F
    }

    return Measurement(convertedValue, unit)
}