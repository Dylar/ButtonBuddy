package de.bitb.buttonbuddy.core.misc

import kotlinx.coroutines.delay
import java.util.*
import kotlin.system.measureTimeMillis

suspend fun atLeast(duration: Long, func: suspend () -> Unit) {
    val runTime = measureTimeMillis { func() } // TODO not working xD
    val remainingTime = duration - runTime
    if (remainingTime > 0) {
        delay(remainingTime)
    }
}

fun timeExceeded(date1: Date, date2: Date, diff: Long): Boolean =
    getMillisecondsBetweenDates(date1, date2) > diff

fun getMillisecondsBetweenDates(date1: Date, date2: Date): Long = date2.time - date1.time

fun getPercentageDiff(milliseconds: Long, targetValue: Long): Float {
    val percentage = (milliseconds.toFloat() / targetValue.toFloat())
    return if (percentage > 1) 1f else if (percentage < 0) 0f else percentage
}
