package com.example.taskscheduler.data.Converters

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds // For easy conversion from Long to Duration
import kotlin.time.DurationUnit

class DurationConverters {

    /**
     * Converts a kotlin.time.Duration object to its total representation in seconds (Long).
     * This Long value will be stored in the database.
     * Returns null if the input duration is null.
     */
    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.inWholeSeconds // Stores the duration as total seconds
        // Alternatively, for milliseconds: return duration?.inWholeMilliseconds
    }

    /**
     * Converts a Long value (representing total seconds) from the database
     * back into a kotlin.time.Duration object.
     * Returns null if the input Long is null.
     */
    @TypeConverter
    fun toDuration(seconds: Long?): Duration? {
        return seconds?.let { value -> value.seconds }
    }
}