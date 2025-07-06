package com.example.taskscheduler.data

import java.util.Calendar
import java.util.Date

data class Session(
    var scheduledTasks: List<ScheduledTask>,
    val startTime: Date,
    val endTime: Date,
) {
    fun isExpired(): Boolean {
        return Date().after(endTime)
    }

}