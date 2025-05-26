package com.example.taskscheduler.data

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.room.TypeConverter
import java.util.Date

class Converters(private val context: Context) {

    // -------- ICON <-> INT --------
    @TypeConverter
    fun iconToInt(icon: Icon?): Int? {
        return icon?.resId
    }

    @TypeConverter
    fun intToIcon(resId: Int?): Icon? {
        return resId?.let { Icon.createWithResource(context, it) }
    }

    // -------- COLOR <-> LONG --------
    @TypeConverter
    fun colorToLong(color: Int?): Long? {
        return color?.toLong()
    }

    @TypeConverter
    fun longToColor(colorLong: Long?): Int? {
        return colorLong?.toInt()
    }

    // -------- DATE <-> LONG --------
    @TypeConverter
    fun dateToLong(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun longToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}