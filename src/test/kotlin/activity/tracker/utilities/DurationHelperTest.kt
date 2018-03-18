package activity.tracker.utilities

import activity.tracker.db.model.Measurement
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationHelperTest {

    @Test
    fun convertMinutesToMinutes() {
        val minutes = convertToMinutes(Measurement(50F, "minutes"))

        assertEquals(50F, minutes)
    }

    @Test
    fun convertSecondsToMinutes() {
        val minutes = convertToMinutes(Measurement(350F, "seconds"))

        assertEquals(5.83, minutes.toDouble(), 0.01)
    }

    @Test
    fun convertInvalidUnitToMinutes() {
        val minutes = convertToMinutes(Measurement(350F, "other"))

        assertEquals(0F, minutes)
    }
}