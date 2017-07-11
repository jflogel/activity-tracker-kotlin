package activity.tracker.controller

import activity.tracker.Activity
import activity.tracker.db.model.MeasurementSum
import activity.tracker.db.model.convertToGoalUnit
import activity.tracker.repository.ActivityRepository
import activity.tracker.utilities.firstDayOfWeek
import activity.tracker.utilities.toEpoch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface WeeklyStatsCreator {
    fun getWeeklyStats(activityId: Int): WeeklyStats
}

@Component
class WeeklyStatsCreatorImpl(@Autowired val repository: ActivityRepository): WeeklyStatsCreator {
    override
    fun getWeeklyStats(activityId: Int): WeeklyStats {
        val activities = repository.findActivities(startOfWeek(), activityId)
        val activity = Activity.getById(activityId)

        val weeklyTotal = activities.fold(MeasurementSum(0f, activity.desiredUnit), { acc, model ->
            val activityGoalValue = convertToGoalUnit(model, activity.goalType)
            MeasurementSum(acc.value + activityGoalValue, acc.unit)
        })
        return WeeklyStats(weeklyTotal)
    }

    fun startOfWeek() = firstDayOfWeek().atStartOfDay().toEpoch()
}

data class WeeklyStats(val weeklyTotal: MeasurementSum)