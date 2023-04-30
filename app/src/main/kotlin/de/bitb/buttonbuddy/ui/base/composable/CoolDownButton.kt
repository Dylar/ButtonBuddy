package de.bitb.buttonbuddy.ui.base.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import de.bitb.buttonbuddy.core.misc.getMillisecondsBetweenDates
import de.bitb.buttonbuddy.core.misc.getPercentageDiff
import de.bitb.buttonbuddy.ui.base.styles.BaseColors
import kotlinx.coroutines.delay
import java.util.*

@Composable
fun CoolDownButton(
    timestamp: Date,
    cooldown: Long,
    button: @Composable BoxScope.() -> Unit
) {
    var progress by remember(timestamp) { mutableStateOf(1f) }
    LaunchedEffect(progress) {
        val diff = getMillisecondsBetweenDates(timestamp, Date())
        progress = 1 - getPercentageDiff(diff, cooldown)
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
            color = BaseColors.DarkGray,
            progress = progress,
            strokeWidth = 40.dp
        )
    }
}