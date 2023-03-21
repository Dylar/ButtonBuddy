package de.bitb.buttonbuddy.ui.base.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.bitb.buttonbuddy.core.misc.getMillisecondsBetweenDates
import de.bitb.buttonbuddy.core.misc.getPercentageDiff
import de.bitb.buttonbuddy.usecase.message.COOLDOWN
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun CoolDownButton(
    timestamp: Date,
    button: @Composable BoxScope.() -> Unit
) {
    var progress by remember(timestamp) { mutableStateOf(1f) }
    LaunchedEffect(progress) {
        val diff = getMillisecondsBetweenDates(timestamp, Date())
        progress = 1 - getPercentageDiff(diff, COOLDOWN)
        delay(10000L)
    }
    Box(
        modifier = Modifier.size(68.dp),
        contentAlignment = Alignment.Center,
    ) {
        button()
        CircularProgressIndicator(
            modifier = Modifier
                .size(56.dp)
                .alpha(0.4f),
            color = Color.Gray,
            progress = progress,
            strokeWidth = 40.dp
        )
    }
}