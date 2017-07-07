package activity.tracker.db.model

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

fun convertToGoalUnit(activityModel: ActivityModel, goalType: String): Float {
    return when (goalType) {
        DISTANCE -> activityModel.distance?.value ?: 0f
        DURATION -> activityModel.time_duration?.value ?: 0f
        COUNT -> 1f
        else -> 0f
    }
}