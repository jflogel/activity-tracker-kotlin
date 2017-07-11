package activity.tracker.controller

import activity.tracker.Activity
import activity.tracker.db.model.ActivityModel
import activity.tracker.db.model.Measurement
import activity.tracker.repository.ActivityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.bind.annotation.RequestMethod.POST
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class EditActivityController(@Autowired val activityRepository: ActivityRepository) {

    @RequestMapping(path = arrayOf("/activity/edit"), method = arrayOf(GET))
    fun getActivity(@RequestParam(required = false) activity: String?): Map<String, Any> {
        val map = hashMapOf<String, Any>()
        map.put("activities", Activity.getAllActivities())
        if (activity != null && !activity.isBlank()) {
            val activityModel = activityRepository.findById(activity)
            if (activityModel != null) {
                map.put("model", ActivityDetailDto(
                        activity,
                        Activity.getById(activityModel.activity_id).description ?: "",
                        activityModel.datetime,
                        activityModel.time_duration?.value ?: 0f,
                        activityModel.distance?.value,
                        activityModel.distance?.unit
                ))
            }
        }
        return map
    }

    @RequestMapping(path = arrayOf("/activity/save"), method = arrayOf(POST))
    fun saveActivity(@RequestBody request: ActivityRequest): Map<String, Any> {
        val errors = validate(request)
        if (errors.isNotEmpty()) {
            return hashMapOf("errors" to errors)
        }

        val activityModel = ActivityModel(
                request.date,
                Activity.getByDescription(request.activity).id!!,
                createDistanceMeasurement(request),
                Measurement(request.time_duration!!.toFloat(), "minutes"),
                request.id
        )
        activityRepository.save(activityModel)
        return hashMapOf()
    }

    private fun createDistanceMeasurement(request: ActivityRequest): Measurement? {
        if (request.distance == null || request.distance_units == null) {
            return null
        }
        return Measurement(request.distance, request.distance_units)
    }

    private fun validate(request: ActivityRequest): List<String> {
        val errors = mutableListOf<String>()

        if (request.activity == null) {
            errors.add("Please select an activity.")
        }

        if (request.time_duration == 0) {
            errors.add("Please enter a duration.")
        }
        return errors
    }
}

data class ActivityRequest(val date: Long,
                           val activity: String?,
                           val time_duration: Int?,
                           val distance: Float?,
                           val distance_units: String?,
                           val id: String?) {
    constructor() : this(0, null, null, null, null, null)
}