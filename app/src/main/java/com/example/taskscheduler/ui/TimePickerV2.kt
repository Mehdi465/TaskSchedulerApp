package com.example.taskscheduler.ui

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskscheduler.R
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

    // 5 minutes steps increments
    val fiveMinutesStep : Float = 360f/288f // 360 degre, 288x5 minutes in 24h

    val initialStartAngle = timeToAngle(initialStartTime)
    val initialEndAngle = timeToAngle(initialEndTime)

    var startAngle by remember { mutableStateOf(initialStartAngle) }
    var endAngle by remember { mutableStateOf(initialEndAngle) }
    var draggingHandle by remember { mutableStateOf<HandleType?>(null) }

    val sweep = ((endAngle - startAngle + 360f) % 360f)

    var startTime = angleToTimeInt(startAngle)
    var endTime = angleToTimeInt(endAngle)


    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(24.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        draggingHandle = null
                                },
                    onDrag = { change, _ ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val angle = floorWithStep(getAngle(center, change.position),fiveMinutesStep)
                        Log.d("angle", "angle: ${floorWithStep(getAngle(center, change.position),fiveMinutesStep)}")
                        when (draggingHandle) {
                            HandleType.START -> startAngle = floorWithStep(angle,fiveMinutesStep)
                            HandleType.END -> endAngle = floorWithStep(angle,fiveMinutesStep)
                            null -> {
                                if (isNear(angle, startAngle)) draggingHandle = HandleType.START
                                else if (isNear(angle, endAngle)) draggingHandle = HandleType.END
                            }
                        }

                        val startTime = angleToTimeInt(startAngle)
                        val endTime = angleToTimeInt(endAngle)

                        onTimeChange(startTime, endTime)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.4f
            val arcThickness = 35f

            //  Graduation 0 -> 23
            val newRadius = size.minDimension / 2.3f
            for (i in 0 until 24) {
                val angleRad = Math.toRadians((i * 15f - 90).toDouble())

                val tick_center = center.plus(Offset(0f,-2f))
                // tick drawing
                val tickStart = Offset(
                    x = tick_center.x + cos(angleRad).toFloat() * (newRadius + 10f),
                    y = tick_center.y + sin(angleRad).toFloat() * (newRadius + 10f)
                )
                val tickEnd = Offset(
                    x = tick_center.x + cos(angleRad).toFloat() * (newRadius + 20f),
                    y = tick_center.y + sin(angleRad).toFloat() * (newRadius + 20f)
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
                            color = android.graphics.Color.LTGRAY
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 40f
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

            // Session filled arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFFFF9800), Color(0xFFFFC107)),
                    center = center
                ),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = arcThickness, cap = StrokeCap.Round)
            )

            // Draw handles
            drawHandleWithIcon(center, radius, floorWithStep(startAngle,fiveMinutesStep), Color(0xFFFFC107), Icons.Default.Build)
            drawHandleWithIcon(center, radius,  floorWithStep(endAngle,fiveMinutesStep), Color(0xFFFF8000), Icons.Default.Add)
        }

        // Center time info
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.LightGray)
            Text(stringResource(R.string.session_starts_at) + " ${displayTime(startTime)}",
                fontSize = 20.sp, color = Color.LightGray)
            Spacer(Modifier.height(8.dp))
            Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color.LightGray)
            Text(stringResource(R.string.session_ends_at) +  " ${displayTime(endTime)}",
                fontSize = 20.sp, color = Color.LightGray)
        }
    }
}

private enum class HandleType { START, END }

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
    if (time < 0 || time >= 24 * 60) {}
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

fun floorWithStep(value: Float, step: Float): Float {
    if (step < 0f){
        throw IllegalArgumentException("Step cannot be negative")
    }
    return (value/step).toInt() * step
}

@Composable
@Preview
fun Preview(){
    TimePickerV2(modifier = Modifier,0, 0, {_,_ ->})
}