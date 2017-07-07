package activity.tracker.utilities

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

fun Float.formatNumber(): String {
    val formatter = NumberFormat.getInstance(Locale.US)
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 0
    formatter.roundingMode = RoundingMode.HALF_DOWN
    return formatter.format(this)
}