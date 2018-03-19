package activity.tracker.utilities

import activity.tracker.db.model.Measurement
import org.junit.Assert.assertEquals
import org.junit.Test

class DurationHelperTest {

    @Test
    fun convertMinutesToMinutes() {
        val minutes = convertDuration(Measurement(50F, "minutes"), "minutes")

        assertEquals(50F, minutes.value)
        assertEquals("minutes", minutes.unit)
    }

    @Test
    fun convertSecondsToMinutes() {
        val minutes = convertDuration(Measurement(350F, "seconds"), "minutes")

        assertEquals(5.83, minutes.value.toDouble(), 0.01)
        assertEquals("minutes", minutes.unit)
    }

    @Test
    fun convertInvalidUnitToMinutes() {
        val minutes = convertDuration(Measurement(350F, "other"), "minutes")

        assertEquals(0F, minutes.value)
        assertEquals("minutes", minutes.unit)
    }
}