package activity.tracker

import activity.tracker.Goal.COUNT
import activity.tracker.Goal.DISTANCE
import activity.tracker.Goal.DURATION

enum class Activity(val id: Int?, val description: String?, val goalType: String, val desiredUnit: String) {
    UNKNOWN(null, null, "", ""),
    EMPTY(null, "", "", ""),
    RUNNING(1, "Running", DISTANCE, "miles"),
    CORE(2, "Core", DURATION, "minutes"),
    SWIMMING(3, "Swimming", DISTANCE, "meters"),
    WEIGHTS(4, "Weights", COUNT, "sessions");

    companion object {
        fun getById(id: Int?): Activity {
            return Activity.values().first { it.id == id } ?: UNKNOWN
        }

        fun getByDescription(description: String?): Activity {
            return Activity.values().first { it.description.equals(description, true) }
        }

        fun getAllActivities(): List<Activity> {
            return Activity.values().filter { it.id != null }
        }
    }
}