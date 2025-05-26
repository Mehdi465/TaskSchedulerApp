package com.example.taskscheduler.data

import java.util.Calendar
import java.util.Date

data class Session(
    var tasks: List<ScheduledTask>,
    val startDate: Date,
    val endDate: Date,
) {
    fun isExpired(): Boolean {
        return Date().after(endDate)
    }

    fun randomizeTasks(){
        this.tasks = this.tasks.shuffled()
    }

    companion object {
        val start = Date()

        val calendar = Calendar.getInstance().apply {
            time = start
            add(Calendar.HOUR_OF_DAY, 2)
            add(Calendar.MINUTE, 15)
        }

        val sessionWithDefaults = Session(
            tasks = ScheduledTask.DEFAULT_TASKS_SCHEDULED,
            startDate = start,
            endDate = calendar.time
        )
    }
}