package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName="sessions")
data class SessionTracking(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var sessionCount: Int,
    // var totalTimeMillisSpent: Long,
    // TODO: val usageDates: List<Date> // List of dates when the session was started
) {
}