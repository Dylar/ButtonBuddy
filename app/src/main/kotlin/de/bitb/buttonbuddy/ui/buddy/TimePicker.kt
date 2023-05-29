package de.bitb.buttonbuddy.ui.buddy

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import de.bitb.buttonbuddy.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val backgroundColor = Color(49, 52, 58)
private val primaryColor = Color(68, 71, 70)
private val secondaryColor = Color(68, 71, 70)
private val selectedColor = Color(104, 220, 255, 255)

object TimePickerTags {

    const val TIME_PICKER_DIALOG = "CooldownPickerDialog"
    const val TIME_PICKER_CANCEL = "CooldownPickerCancel"
    const val TIME_PICKER_OK = "CooldownPickerOK"

    const val TIME_PICKER_HOUR = "CooldownPickerHour"
    const val TIME_PICKER_MIN = "CooldownPickerMin"

    fun timePickerMark(text: String): String = "CooldownPickerMark_$text"
}

enum class TimePart { Hour, Minute }

private const val step = PI * 2 / 12
private fun angleForIndex(hour: Int) = -PI / 2 + step * hour

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun TimerPickerPreview() {
    Box(contentAlignment = Alignment.Center) {
        TimerPicker(
            hour = 0,
            min = 0,
            onCancel = {},
            onSelected = { _, _ -> },
        )
    }
}

@Composable
fun TimerPicker(
    hour: Int,
    min: Int,
    onCancel: () -> Unit,
    onSelected: (Int, Int) -> Unit,
) {
    var selectedPart by remember { mutableStateOf(TimePart.Hour) }
    var selectedHour by remember { mutableStateOf(hour) }
    var selectedMinute by remember { mutableStateOf(min) }
    var previousSelectedHour by remember { mutableStateOf(hour) }
    var previousSelectedMinute by remember { mutableStateOf(min) }

    val previousSelectedTime by remember {
        derivedStateOf { if (selectedPart == TimePart.Hour) previousSelectedHour else previousSelectedMinute / 5 }
    }
    val selectedTime by remember {
        derivedStateOf { if (selectedPart == TimePart.Hour) selectedHour else selectedMinute / 5 }
    }
    val selectTime: (Int) -> Unit = remember {
        {
            if (selectedPart == TimePart.Hour) {
                previousSelectedHour = selectedHour
                selectedHour = it
//                selectedPart = TimePart.Minute
            } else {
                previousSelectedMinute = selectedMinute
                selectedMinute = it * 5
//                selectedPart = TimePart.Hour
            }
        }
    }
    Dialog(onDismissRequest = onCancel) {
        Box(
            Modifier
                .testTag(TimePickerTags.TIME_PICKER_DIALOG)
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(stringResource(R.string.select_time))
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.align(Alignment.CenterHorizontally))
                {
                    TimeCard(
                        tag = TimePickerTags.TIME_PICKER_HOUR,
                        time = selectedHour,
                        isSelected = selectedPart == TimePart.Hour,
                        onClick = { selectedPart = TimePart.Hour }
                    )
                    Text(
                        text = ":",
                        fontSize = 26.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    TimeCard(
                        tag = TimePickerTags.TIME_PICKER_MIN,
                        time = selectedMinute,
                        isSelected = selectedPart == TimePart.Minute,
                        onClick = { selectedPart = TimePart.Minute }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Clock(
                    selectedTime = selectedTime,
                    previousSelectedTime = previousSelectedTime,
                    modifier = Modifier
                        .size(190.dp)
                        .align(Alignment.CenterHorizontally)
                ) { ClockMarks24h(selectedPart, selectedTime, selectTime) }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onCancel,
                        modifier = Modifier.testTag(TimePickerTags.TIME_PICKER_CANCEL)
                    ) { Text(text = stringResource(R.string.cancel), color = selectedColor) }
                    TextButton(
                        onClick = { onSelected(selectedHour, selectedMinute) },
                        modifier = Modifier.testTag(TimePickerTags.TIME_PICKER_OK)
                    ) { Text(text = stringResource(R.string.ok), color = selectedColor) }
                }
            }
        }
    }
}

@Composable
fun Clock(
    modifier: Modifier = Modifier,
    selectedTime: Int,
    previousSelectedTime: Int,
    content: @Composable () -> Unit
) {
    var radiusPx by remember { mutableStateOf(0) }
    var radiusInsidePx by remember { mutableStateOf(0) }

    val distance = abs(selectedTime % 12 - previousSelectedTime % 12)
    val selectedLineAngle by animateFloatAsState(
        targetValue = angleForIndex(selectedTime % 12).toFloat(),
        animationSpec = tween(durationMillis = distance * 100, easing = LinearEasing),
    )

    fun posX(index: Int) =
        ((if (index < 12) radiusPx else radiusInsidePx) * cos(angleForIndex(index))).toInt()

    fun posY(index: Int) =
        ((if (index < 12) radiusPx else radiusInsidePx) * sin(angleForIndex(index))).toInt()

    Box(modifier = modifier) {
        Surface(
            color = primaryColor,
            shape = CircleShape,
            modifier = Modifier.fillMaxSize()
        ) {}
        Layout(
            content = content,
            modifier = Modifier
                .padding(4.dp)
                .drawBehind {
                    val lineLength = if (selectedTime < 12) radiusPx else radiusInsidePx
                    val end = Offset(
                        x = size.width / 2 + lineLength * cos(selectedLineAngle),
                        y = size.height / 2 + lineLength * sin(selectedLineAngle)
                    )
                    drawCircle(radius = 9f, color = selectedColor)
                    drawLine(
                        start = center,
                        end = end,
                        color = selectedColor,
                        strokeWidth = 4f
                    )
                    drawCircle(
                        radius = 36f,
                        center = end,
                        color = selectedColor,
                    )
                }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            assert(placeables.count() == 12 || placeables.count() == 24) { "Missing items: should be 12 or 24, is ${placeables.count()}" }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val size = constraints.maxWidth
                val maxSize = maxOf(placeables.maxOf { it.width }, placeables.maxOf { it.height })

                radiusPx = (constraints.maxWidth - maxSize) / 2
                radiusInsidePx = (radiusPx * 0.67).toInt()

                placeables.forEachIndexed { index, placeable ->
                    placeable.place(
                        size / 2 - placeable.width / 2 + posX(index),
                        size / 2 - placeable.height / 2 + posY(index),
                    )
                }
            }
        }
    }
}

@Composable
fun Mark(
    text: String,
    index: Int, // 0..23
    onIndex: (Int) -> Unit,
    isSelected: Boolean
) {
    Text(
        text = text,
        color = if (isSelected) secondaryColor else Color.White,
        modifier = Modifier.testTag(TimePickerTags.timePickerMark(text.padStart(2, '0'))).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onIndex(index) }
        )
    )
}

@Composable
fun TimeCard(
    tag: String,
    time: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        backgroundColor = if (isSelected) selectedColor else secondaryColor,
        modifier = Modifier.testTag(tag).clickable { onClick() }
    ) {
        Text(
            text = time.toString().padStart(2, '0'),
            fontSize = 26.sp,
            color = if (isSelected) secondaryColor else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ClockMarks24h(selectedPart: TimePart, selectedTime: Int, onTime: (Int) -> Unit) {
    if (selectedPart == TimePart.Hour) {
        Mark(text = "0", index = 0, isSelected = selectedTime == 0, onIndex = onTime)
        Mark(text = "1", index = 1, isSelected = selectedTime == 1, onIndex = onTime)
        Mark(text = "2", index = 2, isSelected = selectedTime == 2, onIndex = onTime)
        Mark(text = "3", index = 3, isSelected = selectedTime == 3, onIndex = onTime)
        Mark(text = "4", index = 4, isSelected = selectedTime == 4, onIndex = onTime)
        Mark(text = "5", index = 5, isSelected = selectedTime == 5, onIndex = onTime)
        Mark(text = "6", index = 6, isSelected = selectedTime == 6, onIndex = onTime)
        Mark(text = "7", index = 7, isSelected = selectedTime == 7, onIndex = onTime)
        Mark(text = "8", index = 8, isSelected = selectedTime == 8, onIndex = onTime)
        Mark(text = "9", index = 9, isSelected = selectedTime == 9, onIndex = onTime)
        Mark(text = "10", index = 10, isSelected = selectedTime == 10, onIndex = onTime)
        Mark(text = "11", index = 11, isSelected = selectedTime == 11, onIndex = onTime)
        Mark(text = "12", index = 12, isSelected = selectedTime == 12, onIndex = onTime)
        Mark(text = "13", index = 13, isSelected = selectedTime == 13, onIndex = onTime)
        Mark(text = "14", index = 14, isSelected = selectedTime == 14, onIndex = onTime)
        Mark(text = "15", index = 15, isSelected = selectedTime == 15, onIndex = onTime)
        Mark(text = "16", index = 16, isSelected = selectedTime == 16, onIndex = onTime)
        Mark(text = "17", index = 17, isSelected = selectedTime == 17, onIndex = onTime)
        Mark(text = "18", index = 18, isSelected = selectedTime == 18, onIndex = onTime)
        Mark(text = "19", index = 19, isSelected = selectedTime == 19, onIndex = onTime)
        Mark(text = "20", index = 20, isSelected = selectedTime == 20, onIndex = onTime)
        Mark(text = "21", index = 21, isSelected = selectedTime == 21, onIndex = onTime)
        Mark(text = "22", index = 22, isSelected = selectedTime == 22, onIndex = onTime)
        Mark(text = "23", index = 23, isSelected = selectedTime == 23, onIndex = onTime)
    } else {
        Mark(text = "0", index = 0, isSelected = selectedTime == 0, onIndex = onTime)
        Mark(text = "5", index = 1, isSelected = selectedTime == 1, onIndex = onTime)
        Mark(text = "10", index = 2, isSelected = selectedTime == 2, onIndex = onTime)
        Mark(text = "15", index = 3, isSelected = selectedTime == 3, onIndex = onTime)
        Mark(text = "20", index = 4, isSelected = selectedTime == 4, onIndex = onTime)
        Mark(text = "25", index = 5, isSelected = selectedTime == 5, onIndex = onTime)
        Mark(text = "30", index = 6, isSelected = selectedTime == 6, onIndex = onTime)
        Mark(text = "35", index = 7, isSelected = selectedTime == 7, onIndex = onTime)
        Mark(text = "40", index = 8, isSelected = selectedTime == 8, onIndex = onTime)
        Mark(text = "45", index = 9, isSelected = selectedTime == 9, onIndex = onTime)
        Mark(text = "50", index = 10, isSelected = selectedTime == 10, onIndex = onTime)
        Mark(text = "55", index = 11, isSelected = selectedTime == 11, onIndex = onTime)
    }
}