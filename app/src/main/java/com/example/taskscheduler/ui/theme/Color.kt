package com.example.taskscheduler.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.min

val Purple80 = Color(0xF4A94995)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// TaskColors
val TaskColors = listOf(
    Color(0xFFE57373), // Light Red
    Color(0xFF81C784), // Light Green
    Color(0xFF64B5F6), // Light Blue
    Color(0xFFFFD54F), // Light Yellow
    Color(0xFFBA68C8), // Light Purple
    Color(0xFF4FC3F7), // Light Cyan
    Color(0xFFF06292), // Light Pink
    Color(0xFFA1887F)  // Light Brown
)

val TaskLightColors = listOf(
    Color(0xFFE57373), // Light Red
    Color(0xFF81C784), // Light Green
    Color(0xFF64B5F6), // Light Blue
    Color(0xFFFFD54F), // Light Yellow
    Color(0xFFBA68C8), // Light Purple
    Color(0xFF4FC3F7), // Light Cyan
    Color(0xFFF06292), // Light Pink
    Color(0xFFA1887F)  // Light Brown
)



//Theme Color
// RED
val ThemeRed1 = Color(0xFFB33B15)
val ThemeRed2 = Color(0xFFB88576)
val ThemeRed3 = Color(0xFFA58F44)
val ThemeRedError = Color(0xFFFF5449)

// BLUE
val ThemeBlue1 = Color(0xFF769CDF)
val ThemeBlue2 = Color(0xFF8991A2)
val ThemeBlue3 = Color(0xFFA288A6)
val ThemeBlueError = Color(0xFFFF5449)

// GREEN
val ThemeGreen1 = Color(0xFF63A002)
val ThemeGreen2 = Color(0xFF85976E)
val ThemeGreen3 = Color(0xFF4D9D98)
val ThemeGreenError = Color(0xFFFF5449)


fun Color.lighten(factor: Float = 0.1f): Color {
    val r = min(1f, this.red + (1f - this.red) * factor)
    val g = min(1f, this.green + (1f - this.green) * factor)
    val b = min(1f, this.blue + (1f - this.blue) * factor)
    return Color(red = r, green = g, blue = b, alpha = this.alpha)
}

fun Color.taskLighten():Color{
    return lighten(0.5f)
}