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


    companion object {
        val start = Date()

        val calendar = Calendar.getInstance().apply {
            time = start
            add(Calendar.HOUR_OF_DAY, 2)
            add(Calendar.MINUTE, 15)
        }

        val sessionWithDefaults = Session(
            scheduledTasks = ScheduledTask.DEFAULT_TASKS_SCHEDULED,
            startTime = start,
            endTime = calendar.time
        )
    }
}