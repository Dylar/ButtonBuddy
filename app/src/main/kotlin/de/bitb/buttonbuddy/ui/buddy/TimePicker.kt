package de.bitb.buttonbuddy.ui.buddy

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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val backgroundColor = Color(49, 52, 58)
private val primaryColor = Color(68, 71, 70)
private val secondaryColor = Color(68, 71, 70)
private val selectedColor = Color(104, 220, 255, 255)

@Composable
fun TimerPicker(hour: Int, min: Int, onCancel: () -> Unit, onSelected: (Int, Int) -> Unit) {
    var selectedPart by remember { mutableStateOf(TimePart.Hour) }
    var selectedHour by remember { mutableStateOf(hour) }
    var selectedMinute by remember { mutableStateOf(min) }

    val selectedTime by remember {
        derivedStateOf { if (selectedPart == TimePart.Hour) selectedHour else selectedMinute / 5 }
    }

    val onTime: (Int) -> Unit = remember {
        { if (selectedPart == TimePart.Hour) selectedHour = it else selectedMinute = it * 5 }
    }
    Dialog(onDismissRequest = onCancel) {
        Box(
            Modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Select time")
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    TimeCard(
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
                        time = selectedMinute,
                        isSelected = selectedPart == TimePart.Minute,
                        onClick = { selectedPart = TimePart.Minute }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Clock(
                    time = selectedTime,
                    modifier = Modifier
                        .size(190.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    ClockMarks24h(selectedPart, selectedTime, onTime)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onCancel,
//                        modifier = Modifier
//                            .width(56.dp)
//                            .height(32.dp)
                    ) {
                        Text(
                            text = "Cancel",
//                            fontSize = 10.sp,
                            color = selectedColor,
                        )
                    }
                    TextButton(
                        onClick = { onSelected(selectedHour, selectedMinute) },
//                        modifier = Modifier
//                            .width(36.dp)
//                            .height(32.dp)
                    ) {
                        Text(
                            text = "OK",
//                            fontSize = 10.sp,
                            color = selectedColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Clock(
    time: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var radiusPx by remember { mutableStateOf(0) }
    var radiusInsidePx by remember { mutableStateOf(0) }

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

        val padding = 4.dp
        val hourCirclePx = 36f

        Layout(
            content = content,
            modifier = Modifier
                .padding(padding)
                .drawBehind {
                    val end = Offset(
                        x = size.width / 2 + posX(time),
                        y = size.height / 2 + posY(time)
                    )

                    drawCircle(
                        radius = 9f,
                        color = selectedColor,
                    )

                    drawLine(
                        start = center,
                        end = end,
                        color = selectedColor,
                        strokeWidth = 4f
                    )

                    drawCircle(
                        radius = hourCirclePx,
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
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onIndex(index) }
        )
    )
}

@Composable
fun TimeCard(
    time: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        backgroundColor = if (isSelected) selectedColor else secondaryColor,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = if (time == 0) "00" else time.toString(),
            fontSize = 26.sp,
            color = if (isSelected) secondaryColor else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

enum class TimePart { Hour, Minute }

private const val step = PI * 2 / 12
private fun angleForIndex(hour: Int) = -PI / 2 + step * hour

@Preview(device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun TimerPickerPreview() {
    Box(contentAlignment = Alignment.Center) {
        TimerPicker(0, 0, onSelected = { _, _ -> }, onCancel = {})
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