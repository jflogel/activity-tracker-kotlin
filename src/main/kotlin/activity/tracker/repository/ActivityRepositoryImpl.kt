package activity.tracker.repository

import activity.tracker.db.model.ActivityModel
import org.springframework.stereotype.Component
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

interface ActivityRepository {
    fun findActivities(startDate: Long, activityId: Int? = null): List<ActivityModel>
    fun findMostRecent(): ActivityModel?
    fun findById(id: String): ActivityModel?
    fun save(activityModel: ActivityModel)
    fun delete(activityModel: ActivityModel)
}

@Component
class ActivityRepositoryImpl(val mongoTemplate: MongoTemplate): ActivityRepository {

    override fun findActivities(startDate: Long, activityId: Int?): List<ActivityModel> {
        val query = Query()
        query.addCriteria(Criteria.where("datetime").gte(startDate))
        if (activityId != null) {
            query.addCriteria(Criteria.where("activity_id").`is`(activityId))
        }
        return mongoTemplate.find(query, ActivityModel::class.java)
    }

    override fun findMostRecent(): ActivityModel? {
        val query = Query().with(Sort(Sort.Direction.DESC, "datetime"))
        return mongoTemplate.findOne(query, ActivityModel::class.java)
    }

    override fun findById(id: String): ActivityModel? {
        return mongoTemplate.findById(id, ActivityModel::class.java)
    }

    override fun save(activityModel: ActivityModel) {
        mongoTemplate.save(activityModel)
    }

    override fun delete(activityModel: ActivityModel) {
        mongoTemplate.remove(activityModel)
    }
}