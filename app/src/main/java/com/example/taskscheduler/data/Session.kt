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

    fun getCurrentTask(): ScheduledTask? {
        val currentTime = Calendar.getInstance().time
        return scheduledTasks.find { it.startTime.before(currentTime) && it.endTime.after(currentTime) }
    }

    fun isSessionFinished(): Boolean{
        val currentTime = Calendar.getInstance().time
        return currentTime.after(endTime)
    }
}