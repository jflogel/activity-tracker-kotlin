package activity.tracker.repository

import activity.tracker.db.model.ActivityModel
import activity.tracker.db.model.Measurement
import activity.tracker.utilities.toEpoch
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import java.time.LocalDateTime
import java.time.Month

@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(activity.tracker.Application::class))
@Service
class ActivityRepositoryTest {
    @Autowired
    lateinit var mongoTemplate: MongoTemplate
    @Autowired
    lateinit var activityRepository: ActivityRepositoryImpl

    val distance = Measurement(1750, "miles")
    val timeDuration = Measurement(13, "hours")
    val activity1 = ActivityModel(LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0, 0).toEpoch(), 1, distance, timeDuration, null)
    val activity2 = ActivityModel(LocalDateTime.of(2017, Month.JANUARY, 4, 0, 0, 0).toEpoch(), 2, distance, timeDuration, null)
    val activity3 = ActivityModel(LocalDateTime.of(2017, Month.JANUARY, 2, 0, 0, 0).toEpoch(), 3, distance, timeDuration, null)
    val activity4 = ActivityModel(LocalDateTime.of(2017, Month.JANUARY, 3, 0, 0, 0).toEpoch(), 3, distance, timeDuration, null)
    val activity5 = ActivityModel(LocalDateTime.of(2016, Month.JANUARY, 3, 0, 0, 0).toEpoch(), 3, distance, timeDuration, null)

    @Before
    fun setup() {
        mongoTemplate.dropCollection(activity.tracker.db.model.ActivityModel::class.java)
        mongoTemplate.insertAll(listOf(activity1, activity2, activity3, activity4, activity5))
    }

    @Test
    fun findAllActivitiesByStartDate() {
        val startOfYear = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0, 0).toEpoch()

        val activities = activityRepository.findActivities(startOfYear)

        assertThat(activities.size, Matchers.`is`(4))
        assertThat(activities, Matchers.containsInAnyOrder(activity1, activity2, activity3, activity4))
    }

    @Test
    fun findAllActivitiesByStartDateAndType() {
        val startOfYear = LocalDateTime.of(2017, Month.JANUARY, 1, 0, 0, 0).toEpoch()
        val swimming = 3

        val activities = activityRepository.findActivities(startOfYear, swimming)

        assertThat(activities.size, Matchers.`is`(2))
        assertThat(activities, Matchers.containsInAnyOrder(activity3, activity4))
    }

    @Test
    fun findMostRecentActivity() {
        assertThat(activityRepository.findMostRecent(), `is`(activity2))
    }

    @Test
    fun findActivityById() {
        assertThat(activityRepository.findById(activity4.id!!), `is`(activity4))
    }

    @Test
    fun save() {
        val newActivity = ActivityModel(LocalDateTime.of(2017, Month.MAY, 28, 0, 0, 0).toEpoch(), 1, distance, timeDuration, null)

        activityRepository.save(newActivity)

        val actual = mongoTemplate.findById(newActivity.id, ActivityModel::class.java)
        assertThat(actual, `is`(newActivity))
    }

    @Test
    fun delete() {
        val newActivity = ActivityModel(LocalDateTime.of(2017, Month.JULY, 28, 0, 0, 0).toEpoch(), 1, distance, timeDuration, null)
        mongoTemplate.save(newActivity)

        activityRepository.delete(newActivity)

        assertThat(mongoTemplate.findById(newActivity.id, ActivityModel::class.java), `is`(nullValue()))
    }
}

