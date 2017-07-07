package activity.tracker.controller

import activity.tracker.Activity.*
import activity.tracker.db.model.ActivityModel
import activity.tracker.db.model.Measurement
import activity.tracker.db.model.MeasurementSum
import activity.tracker.repository.ActivityRepository
import activity.tracker.utilities.toEpoch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.anyLong
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.runners.MockitoJUnitRunner
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(MockitoJUnitRunner::class)
class ActivityChartControllerTest {
    @Mock
    lateinit var activityRepository: ActivityRepository
    @Mock
    lateinit var weeklyStatsCreator: WeeklyStatsCreator
    @Mock
    lateinit var yearlyStatsCreator: YearlyStatsCreator
    lateinit var controller: ActivityChartController

    private val DAY_RANGE = 31L

    @Before
    fun setUp() {
        controller = ActivityChartController(activityRepository, weeklyStatsCreator, yearlyStatsCreator)
    }

    @Test
    fun chartForAllActivities() {
        val thirtyOneDaysAgo = LocalDateTime.now().minusDays(DAY_RANGE).toEpoch()

        val dto = controller.chart(null)

        verify(activityRepository).findActivities(thirtyOneDaysAgo, null)
        commonAssertions(dto)
    }

    @Test
    fun chartForOneActivity() {
        val thirtyOneDaysAgo = LocalDateTime.now().minusDays(DAY_RANGE).toEpoch()
        val running = RUNNING
        `when`(weeklyStatsCreator.getWeeklyStats(running.id!!)).thenReturn(WeeklyStats(MeasurementSum(10f, "miles")))
        `when`(yearlyStatsCreator.getYearStats(running.id!!)).thenReturn(YearlyStats(MeasurementSum(214f, "miles"), MeasurementSum(9.3f, "miles"), emptyList()))

        val dto = controller.chart(running.description)

        verify(activityRepository).findActivities(thirtyOneDaysAgo, running.id)
        commonAssertions(dto)
    }

    @Test
    fun shouldGroupAndSumActivitiesForSameDayTogether() {
        val sevenDaysAgo = LocalDateTime.now().minusDays(7)
        val thirtyDaysAgo = LocalDateTime.now().minusDays(DAY_RANGE - 1)
        val today = LocalDateTime.now()
        `when`(activityRepository.findActivities(anyLong(), eq(null))).thenReturn(listOf(
                ActivityModel(today.toEpoch(), RUNNING.id!!, null, Measurement(28f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), RUNNING.id!!, null, Measurement(15f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(15f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), RUNNING.id!!, null, Measurement(5f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(25f, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), WEIGHTS.id!!, null, Measurement(15f, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5f, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(30f, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), WEIGHTS.id!!, null, Measurement(20f, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5f, "minutes"), null)
        ))

        val dto = controller.chart(null)

        assertThat(dto.summaries.find { it.date == formatDate(today) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(today), 28f, 0f, 0f, 0f)))
        assertThat(dto.summaries.find { it.date == formatDate(sevenDaysAgo) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(sevenDaysAgo), 20f, 10f, 40f, 15f)))
        assertThat(dto.summaries.find { it.date == formatDate(thirtyDaysAgo) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(thirtyDaysAgo), 0f, 10f, 30f, 20f)))
    }

    private fun formatDate(date: LocalDateTime) = date.format(DateTimeFormatter.ofPattern("M/d"))

    private fun commonAssertions(dto: ActivityChartDto) {
        assertThat(dto.legendValues, Matchers.contains(
                "Activity",
                RUNNING.description,
                CORE.description,
                SWIMMING.description,
                WEIGHTS.description))
        assertThat(dto.summaries, hasSize(31))
        assertThat(dto.summaries.map { it.date }.toSet(), hasSize(31))
        assertThat(dto.summaries.map { it.date }.first(), `is`(LocalDate.now().minusDays(DAY_RANGE - 1).format(DateTimeFormatter.ofPattern("M/d"))))
        assertThat(dto.summaries.map { it.date }.last(), `is`(LocalDate.now().format(DateTimeFormatter.ofPattern("M/d"))))
    }
}