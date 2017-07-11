package activity.tracker.controller

import activity.tracker.repository.ActivityRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.DELETE
import org.springframework.web.bind.annotation.RestController

@RestController
class DeleteActivityController(val activityRepository: ActivityRepository) {

    @RequestMapping(path = arrayOf("/activity/delete"), method = arrayOf(DELETE))
    fun deleteActivity(id: String) {
        val activityModel = activityRepository.findById(id)
        if (activityModel != null) {
            activityRepository.delete(activityModel)
        }
    }
}