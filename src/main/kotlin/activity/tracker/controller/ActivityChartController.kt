package activity.tracker.controller

import activity.tracker.Activity
import activity.tracker.db.model.ActivityModel
import activity.tracker.utilities.convertDuration
import activity.tracker.utilities.formatNumber
import activity.tracker.utilities.toEpoch
import activity.tracker.utilities.zoneId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RestController
class ActivityChartController(@Autowired val repository: activity.tracker.repository.ActivityRepository,
                              @Autowired val weeklyStatsCreator: WeeklyStatsCreator,
                              @Autowired val yearlyStatsCreator: YearlyStatsCreator) {

    @RequestMapping("/data/activities/chart")
    fun chart(@RequestParam(required = false) activity: String?): ActivityChartDto {
        val activityId = Activity.getByDescription(activity).id
        val numberOfDaysForRecentActivity = 31L
        val thirtyOneDaysAgo = LocalDateTime.now().minusDays(numberOfDaysForRecentActivity).toEpoch()
        val latestActivities = repository.findActivities(thirtyOneDaysAgo, activityId)

        val days = buildListOfAllDays(numberOfDaysForRecentActivity)
        val activitiesGroupedByDate = latestActivities.groupBy { convertEpochToFormattedDate(it.datetime) }
        val summaries: List<ActivityDaySummaryDto> = days.fold(mutableListOf(), { accum, curr ->
            val activitiesForDate = activitiesGroupedByDate.getOrDefault(curr, emptyList())
            val summaryForDate: ActivityDaySummaryDto = activitiesForDate.fold(ActivityDaySummaryDto(curr, 0f, 0f, 0f, 0f), { accum, x ->
                accum.add(x)
            })
            accum.add(summaryForDate)
            accum
        })

        if (activityId != null) {
            val activityDescription = Activity.getById(activityId).description ?: ""
            val weeklyStats = weeklyStatsCreator.getWeeklyStats(activityId)
            val yearStats = yearlyStatsCreator.getYearStats(activityId)

            val activityStats = ActivityStatsDto(
                    "${weeklyStats.weeklyTotal.value.formatNumber()} ${weeklyStats.weeklyTotal.unit}",
                    "${yearStats.weeklyAverage.value.formatNumber()} ${yearStats.weeklyAverage.unit}",
                    "${yearStats.yearTotal.value.formatNumber()} ${yearStats.yearTotal.unit}"
            )
            val activities: List<ActivityDetailDto> = yearStats.allActivities.map { ActivityDetailDto(it.id ?: "", activityDescription, it.datetime, it.time_duration?.value ?: 0f, it.time_duration?.unit ?: "", it.distance?.value, it.distance?.unit ?: "") }
            return ActivityChartDto(summaries, getLegendValues(), activityStats, activities)
        }

        return ActivityChartDto(summaries, getLegendValues(), null, emptyList())
    }

    private fun buildListOfAllDays(days: Long): List<String> {
        val formatter = dateTimeFormatter()
        return (0 until days).map { LocalDate.now().minusDays(it).format(formatter) }.reversed()
    }

    private fun dateTimeFormatter() = DateTimeFormatter.ofPattern("M/d")

    private fun convertEpochToFormattedDate(epoch: Long): String {
        return Instant.ofEpochSecond(epoch).atZone(zoneId()).format(dateTimeFormatter())
    }

    private fun getLegendValues(): MutableList<String> {
        val legendValues = mutableListOf("Activity")
        Activity.getAllActivities().forEach { legendValues.add(it.description!!) }
        return legendValues
    }
}

data class ActivityChartDto(val summaries: List<ActivityDaySummaryDto>,
                            val legendValues: List<String>,
                            val activityStats: ActivityStatsDto?,
                            val activities: List<ActivityDetailDto>)

data class ActivityStatsDto(val totalForWeek: String, val average: String, val totalForYear: String)
data class ActivityDetailDto(val id: String, val activity: String, val date: Long, val time_duration: Float, val time_duration_units: String, val distance: Float?, val distance_units: String?)
data class ActivityDaySummaryDto(val date: String, val running: Float, val core: Float, val swimming: Float, val weights: Float) {
    fun add(activityModel: ActivityModel): ActivityDaySummaryDto {
        val durationInMinutes = convertDuration(activityModel.time_duration!!, "minutes").value
        return when (activityModel.activity_id) {
            1 -> ActivityDaySummaryDto(this.date, this.running + durationInMinutes, this.core, this.swimming, this.weights)
            2 -> ActivityDaySummaryDto(this.date, this.running, this.core + durationInMinutes, this.swimming, this.weights)
            3 -> ActivityDaySummaryDto(this.date, this.running, this.core, this.swimming + durationInMinutes, this.weights)
            4 -> ActivityDaySummaryDto(this.date, this.running, this.core, this.swimming, this.weights + durationInMinutes)
            else -> this
        }
    }
}