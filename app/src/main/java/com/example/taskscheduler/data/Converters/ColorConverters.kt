package com.example.taskscheduler.data.Converters

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb // For Color -> Int
import androidx.room.TypeConverter

class ColorConverters {

    /**
     * Converts an androidx.compose.ui.graphics.Color object to its ARGB Long representation.
     * This Long value will be stored in the database.
     * Returns null if the input color is null.
     */
    @TypeConverter
    fun fromColor(color: Color?): Long? {
        // Convert the Compose Color to an ARGB Int, then to Long for database storage.
        // Using toArgb() gives an Int. SQLite can store Long, so converting to Long is fine.
        return color?.toArgb()?.toLong()
    }

    /**
     * Converts a Long value (representing an ARGB color) from the database
     * back into an androidx.compose.ui.graphics.Color object.
     * Returns null if the input Long is null.
     */
    @TypeConverter
    fun toColor(argbLong: Long?): Color? {
        // Convert the Long from the database back to an Int, then create a Compose Color.
        return argbLong?.let { Color(it.toInt()) }
    }
}