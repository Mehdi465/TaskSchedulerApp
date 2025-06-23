package com.example.taskscheduler.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimePickerV2(
    modifier: Modifier = Modifier,
    initialStartTime: Int,
    initialEndTime: Int,
    onTimeChange: (Int, Int) -> Unit
) {

    val initialSleepAngle = timeToAngle(initialStartTime)
    val initialWakeAngle = timeToAngle(initialEndTime)

    var sleepAngle by remember { mutableStateOf(initialSleepAngle) }
    var wakeAngle by remember { mutableStateOf(initialWakeAngle) }
    var draggingHandle by remember { mutableStateOf<HandleType?>(null) }

    val sweep = ((wakeAngle - sleepAngle + 360f) % 360f)

    val sleepTime = angleToTimeInt(sleepAngle)
    val wakeTime = angleToTimeInt(wakeAngle)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(24.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        draggingHandle = null
                        onTimeChange(sleepTime, wakeTime)
                                },
                    onDrag = { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val angle = getAngle(center, change.position)
                        when (draggingHandle) {
                            HandleType.SLEEP -> sleepAngle = angle
                            HandleType.WAKE -> wakeAngle = angle
                            null -> {
                                if (isNear(angle, sleepAngle)) draggingHandle = HandleType.SLEEP
                                else if (isNear(angle, wakeAngle)) draggingHandle = HandleType.WAKE
                            }
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.5f
            val arcThickness = 40f

            //  Graduation 0 -> 23
            val newRadius = size.minDimension / 2.4f
            for (i in 0 until 24) {
                val angleRad = Math.toRadians((i * 15f - 90).toDouble())
                val tickStart = Offset(
                    x = center.x + cos(angleRad).toFloat() * (newRadius + 10f),
                    y = center.y + sin(angleRad).toFloat() * (newRadius + 10f)
                )
                val tickEnd = Offset(
                    x = center.x + cos(angleRad).toFloat() * (newRadius + 20f),
                    y = center.y + sin(angleRad).toFloat() * (newRadius + 20f)
                )
                drawLine(Color.Gray, tickStart, tickEnd, strokeWidth = 2f)

                val textAngle = angleRad
                val labelX = center.x + cos(textAngle).toFloat() * (newRadius + 35f)
                val labelY = center.y + sin(textAngle).toFloat() * (newRadius + 35f)
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        "$i",
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 28f
                            isAntiAlias = true
                        }
                    )
                }
            }

            // Base arc background
            drawCircle(
                color = Color(0xFFEFEFEF),
                center = center,
                radius = radius,
                style = Stroke(width = arcThickness)
            )

            // Sleep filled arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFC107)),
                    center = center
                ),
                startAngle = sleepAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = arcThickness, cap = StrokeCap.Round)
            )

            // Draw handles
            drawHandleWithIcon(center, radius, sleepAngle, Color(0xFFFF9800), Icons.Default.Build)
            drawHandleWithIcon(center, radius, wakeAngle, Color(0xFFFFC107), Icons.Default.Add)
        }

        // Center time info
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black)
            Text("Session starts at ${displayTime(sleepTime)}", fontSize = 20.sp, color = Color.Black)
            Spacer(Modifier.height(8.dp))
            Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color.Black)
            Text("Session ends at ${displayTime(wakeTime)}", fontSize = 20.sp, color = Color.Black)
        }
    }
}

private enum class HandleType { SLEEP, WAKE }

private fun angleToTimeString(angle: Float): String {
    val totalMinutes = ((angle + 90) % 360) * 4
    val hour = (totalMinutes / 60).toInt()
    val minute = (totalMinutes % 60).toInt()
    return "%d:%02d".format(hour % 24, minute)
}

private fun displayTime(time:Int):String{
    return "%02d:%02d".format(time / 60, time % 60)
}

private fun angleToTimeInt(angle: Float): Int {
    val totalMinutes = ((angle + 90) % 360) * 4
    val hour = (totalMinutes / 60).toInt()
    val minute = (totalMinutes % 60).toInt()
    return hour * 60 + minute
}

private fun timeToAngle(time: Int): Float {
    if (time < 0 || time >= 24 * 60) {} // TODO: Handle invalid time
    return (time / 4.0f) - 90.0f
}

private fun getAngle(center: Offset, touch: Offset): Float {
    val dx = touch.x - center.x
    val dy = touch.y - center.y
    return ((atan2(dy, dx).toDegrees() + 360f) % 360f)
}

private fun isNear(angle1: Float, angle2: Float): Boolean {
    val diff = abs(angle1 - angle2)
    return diff < 15f || diff > 345f
}

private fun Float.toDegrees() = Math.toDegrees(this.toDouble()).toFloat()

private fun DrawScope.drawHandleWithIcon(center: Offset, radius: Float, angle: Float, color: androidx.compose.ui.graphics.Color, icon: ImageVector) {
    val rad = Math.toRadians(angle.toDouble())
    val x = center.x + cos(rad).toFloat() * radius
    val y = center.y + sin(rad).toFloat() * radius
    drawCircle(color, radius = 28f, center = Offset(x, y))
    // You can overlay Compose Icon composable here for more accurate visuals
}

@Composable
@Preview
fun preview(){
    TimePickerV2(modifier = Modifier,0, 0, {_,_ ->})
}