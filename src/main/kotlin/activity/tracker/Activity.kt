package activity.tracker

enum class Activity(val id: Int?, val description: String?) {
    UNKNOWN(null, null),
    EMPTY(null, ""),
    RUNNING(1, "Running"),
    CORE(2, "Core"),
    SWIMMING(3, "Swimming"),
    WEIGHTS(4, "Weights");

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