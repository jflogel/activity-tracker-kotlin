package activity.tracker.db.model

import activity.tracker.Activity
import activity.tracker.Goal.COUNT
import activity.tracker.Goal.DISTANCE
import activity.tracker.Goal.DURATION
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "activity_tracking")
data class ActivityModel(val datetime: Long,
                         val activity_id: Int,
                         val distance: activity.tracker.db.model.Measurement?,
                         val time_duration: activity.tracker.db.model.Measurement?,
                         @Id var id: String?)

val METERS_IN_MILE: Float = 1609.344f
val YARDS_IN_MILE: Float = 1760f
val YARDS_IN_METER: Float = 1.0936132f

fun convertToGoalTypeAndDesiredUnit(activityModel: ActivityModel, activity: Activity): Measurement {
    return when (activity.goalType) {
        DISTANCE -> convertToDesiredDistanceUnit(activityModel.distance, activity)
        DURATION -> getDuration(activityModel.time_duration, activity)
        COUNT -> Measurement(1f, activity.desiredUnit)
        else -> Measurement(0f, activity.desiredUnit)
    }
}

private fun getDuration(measurement: Measurement?, activity: Activity): Measurement {
    if (measurement == null) {
        return Measurement(0f, activity.desiredUnit)
    }
    return measurement
}

private fun convertToDesiredDistanceUnit(measurement: Measurement?, activity: Activity): Measurement {
    if (measurement == null) {
        return Measurement(0f, activity.desiredUnit)
    }
    return when (activity.desiredUnit) {
        "meters" -> convertToMeters(measurement)
        "miles" -> convertToMiles(measurement)
        else -> throw Exception("Conversion does not exist for desired activity unit")
    }
}

private fun convertToMiles(measurement: Measurement): Measurement {
    return when (measurement.unit) {
        "miles" -> measurement
        "meters" -> Measurement(measurement.value / METERS_IN_MILE, "miles")
        "yards" -> Measurement(measurement.value / YARDS_IN_MILE, "miles")
        else -> throw Exception("Couldn't convert measurement to miles")
    }
}

private fun convertToMeters(measurement: Measurement): Measurement {
    return when (measurement.unit) {
        "meters" -> measurement
        "miles" -> Measurement(measurement.value * METERS_IN_MILE, "meters")
        "yards" -> Measurement(measurement.value / YARDS_IN_METER, "meters")
        else -> throw Exception("Couldn't convert measurement to meters")
    }
}
