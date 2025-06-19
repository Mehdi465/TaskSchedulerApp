package com.example.taskscheduler.data

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import com.example.taskscheduler.R
import java.util.Date
import kotlin.time.Duration

class ScheduledTask(
    val task: Task,
    val startDate: Date,
    val endDate: Date,
    var isCompleted: Boolean = false
){
    fun isCurrentlyActive(): Boolean{
        val currentDate = Date()
        return currentDate.after(startDate) && currentDate.before(endDate)
    }

    val name: String
        get() = task.name

    fun scheduleTask(tasks:List<Task>,duration: Duration):List<ScheduledTask>{
        return listOf()
    }

    companion object{
        val PREPARATION_TASK_SCHEDULED = ScheduledTask(task = Task.PREPARATION_TASK,Date(System.currentTimeMillis()),Date(System.currentTimeMillis() + 60 * 60 * 1000))

        val EXECUTION_TASK_SCHEDULED = ScheduledTask(task = Task.EXECUTION_TASK,Date(System.currentTimeMillis() + 60 * 60 * 1000),Date(System.currentTimeMillis() + 210 * 60 * 1000))

        val REVIEW_TASK_SCHEDULED = ScheduledTask(task = Task.REVIEW_TASK,Date(System.currentTimeMillis() + 210 * 60 * 1000),Date(System.currentTimeMillis() + 215 * 60 * 1000))

        val COMPLETION_TASK_SCHEDULED = ScheduledTask(task = Task.COMPLETION_TASK,Date(System.currentTimeMillis() + 215 * 60 * 1000),Date(System.currentTimeMillis() + 235 * 60 * 1000))

        val DEFAULT_TASKS_SCHEDULED = listOf(
            PREPARATION_TASK_SCHEDULED,
            EXECUTION_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED,
            COMPLETION_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED,
            REVIEW_TASK_SCHEDULED
        )
    }

}