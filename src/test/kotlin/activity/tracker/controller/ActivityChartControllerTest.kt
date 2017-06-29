package activity.tracker.controller

import activity.tracker.Activity.*
import activity.tracker.db.model.ActivityModel
import activity.tracker.db.model.Measurement
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
    lateinit var controller: ActivityChartController

    private val DAY_RANGE = 31L

    @Before
    fun setUp() {
        controller = ActivityChartController(activityRepository)
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
                ActivityModel(today.toEpoch(), RUNNING.id!!, null, Measurement(28, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), RUNNING.id!!, null, Measurement(15, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(15, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), RUNNING.id!!, null, Measurement(5, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(25, "minutes"), null),
                ActivityModel(sevenDaysAgo.toEpoch(), WEIGHTS.id!!, null, Measurement(15, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), SWIMMING.id!!, null, Measurement(30, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), WEIGHTS.id!!, null, Measurement(20, "minutes"), null),
                ActivityModel(thirtyDaysAgo.toEpoch(), CORE.id!!, null, Measurement(5, "minutes"), null)
        ))

        val dto = controller.chart(null)

        assertThat(dto.summaries.find { it.date == formatDate(today) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(today), 28, 0, 0, 0)))
        assertThat(dto.summaries.find { it.date == formatDate(sevenDaysAgo) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(sevenDaysAgo), 20, 10, 40, 15)))
        assertThat(dto.summaries.find { it.date == formatDate(thirtyDaysAgo) }, samePropertyValuesAs(ActivityDaySummaryDto(formatDate(thirtyDaysAgo), 0, 10, 30, 20)))
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