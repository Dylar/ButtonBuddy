package de.bitb.buttonbuddy.core.misc

import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

suspend fun atLeast(duration: Long, func: suspend () -> Unit) {
    val runTime = measureTimeMillis { func() } // TODO not working xD
    val remainingTime = duration - runTime
    if (remainingTime > 0) {
        delay(remainingTime)
    }
}
