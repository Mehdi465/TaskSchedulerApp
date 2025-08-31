package com.example.taskscheduler.data.Converters

import androidx.compose.ui.input.key.type
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class DateConverters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Converters for List<Date> using Gson (you can use other serialization methods too)
    @TypeConverter
    fun fromDateListString(value: String?): List<Date>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<Date>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun dateListToString(list: List<Date>?): String? {
        if (list == null) {
            return null
        }
        return Gson().toJson(list)
    }

    // and so on
}