package de.bitb.buttonbuddy

import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

suspend fun atLeast(duration: Long, func: () -> Unit) {
    val runTime = measureTimeMillis { func() }
    val remainingTime = duration - runTime
    if (remainingTime > 0) {
        delay(remainingTime)
    }
}
