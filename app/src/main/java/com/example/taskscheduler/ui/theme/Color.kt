package com.example.taskscheduler.ui.theme

import android.graphics.Color as GraphicColor
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.min
import androidx.compose.ui.graphics.toArgb
import android.graphics.Color as AndroidColor // Alias to avoid confusion
import kotlin.math.max
import kotlin.math.min

val ThemeColor = Color(0xFF6200EE)
val SecondThemeColor = Color(0xFFB196BE)
val White = Color(0xFFFFFFFF)
val BlackText = Color(0xFF747272)
val DarkLight = Color(0xFFA19AA1)


val MidDark = Color(0x22FFFFFF)
val Charcoal = Color(0xFF1C1B1F)
val LightDark = Color(0x66CDCDCD)


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