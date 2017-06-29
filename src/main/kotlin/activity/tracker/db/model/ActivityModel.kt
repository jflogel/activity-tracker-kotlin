package activity.tracker.db.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "activity_tracking")
data class ActivityModel(val datetime: Long,
                         val activity_id: Int,
                         val distance: activity.tracker.db.model.Measurement?,
                         val time_duration: activity.tracker.db.model.Measurement?,
                         @Id var id: String?)