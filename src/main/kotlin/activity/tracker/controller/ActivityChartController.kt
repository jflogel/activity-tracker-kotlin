package activity.tracker.controller

import activity.tracker.Activity
import activity.tracker.db.model.ActivityModel
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
class ActivityChartController(@Autowired val repository: activity.tracker.repository.ActivityRepository) {

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
            val summaryForDate: ActivityDaySummaryDto = activitiesForDate.fold(ActivityDaySummaryDto(curr, 0, 0, 0, 0), { accum, x ->
                accum.add(x)
            })
            accum.add(summaryForDate)
            accum
        })
        return ActivityChartDto(summaries, getLegendValues())
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

data class ActivityChartDto(val summaries: List<ActivityDaySummaryDto>, val legendValues: List<String>)
data class ActivityDaySummaryDto(val date: String, val running: Int, val core: Int, val swimming: Int, val weights: Int) {
    fun add(activityModel: ActivityModel): ActivityDaySummaryDto {
        return when (activityModel.activity_id) {
            1 -> ActivityDaySummaryDto(this.date, this.running + activityModel.time_duration!!.value, this.core, this.swimming, this.weights)
            2 -> ActivityDaySummaryDto(this.date, this.running, this.core + activityModel.time_duration!!.value, this.swimming, this.weights)
            3 -> ActivityDaySummaryDto(this.date, this.running, this.core, this.swimming + activityModel.time_duration!!.value, this.weights)
            4 -> ActivityDaySummaryDto(this.date, this.running, this.core, this.swimming, this.weights + activityModel.time_duration!!.value)
            else -> this
        }
    }
}