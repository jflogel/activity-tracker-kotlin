package activity.tracker.controller

import activity.tracker.Activity
import activity.tracker.db.model.ActivityModel
import activity.tracker.db.model.MeasurementSum
import activity.tracker.db.model.convertToGoalUnit
import activity.tracker.repository.ActivityRepository
import activity.tracker.utilities.firstDayOfYear
import activity.tracker.utilities.toEpoch
import activity.tracker.utilities.weekOfYear
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface YearlyStatsCreator {
    fun getYearStats(activityId: Int): YearlyStats
}

@Component
class YearlyStatsCreatorImpl(@Autowired val repository: ActivityRepository): YearlyStatsCreator {
    override
    fun getYearStats(activityId: Int): YearlyStats {
        val activities = repository.findActivities(startOfYear(), activityId)
        val activity = Activity.getById(activityId)

        val yearTotal = activities.fold(MeasurementSum(0f, activity.desiredUnit), { acc, model ->
            MeasurementSum(acc.value + convertToGoalUnit(model, activity.goalType), acc.unit)
        })
        val weeklyAverage = MeasurementSum(yearTotal.value / weekOfYear(), yearTotal.unit)

        return YearlyStats(yearTotal, weeklyAverage, activities.sortedByDescending { it.datetime })
    }

    fun startOfYear() = firstDayOfYear().atStartOfDay().toEpoch()
}

data class YearlyStats(val yearTotal: MeasurementSum, val weeklyAverage: MeasurementSum, val allActivities: List<ActivityModel>)