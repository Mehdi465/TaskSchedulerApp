package com.example.taskscheduler.data

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.Color
import com.example.taskscheduler.R
import java.util.Date
import kotlin.time.Duration
import java.time.Instant
import java.time.Duration as JavaTimeDuration

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

    companion object{

        fun taskToScheduledTask(tasks: List<Task>,duration: Duration): List<ScheduledTask>{
            var result = mutableListOf<ScheduledTask>()
            var cumulDuration = Duration.ZERO

            // no reducing task duration // TODO: make the last task duration flexible
            for (task in tasks){
                val scheduledTask = ScheduledTask(
                    task,Date(System.currentTimeMillis() + cumulDuration.inWholeMilliseconds),
                    Date(System.currentTimeMillis() + duration.inWholeMilliseconds + cumulDuration.inWholeMilliseconds))
                result.add(scheduledTask)
                cumulDuration += task.duration
            }
            return result
        }

        fun scheduleTask(tasks:List<Task>,startTime:Date,endTime:Date):List<ScheduledTask>{

            // get session duration --------
            val javaSessionDuration : JavaTimeDuration = JavaTimeDuration.between(
                startTime.toInstant(),endTime.toInstant())
            val sessionDuration : Duration = Duration.parse(javaSessionDuration.toString())

            // Scheduling part -------------
            // init final tasks
            val pickedTasks = mutableListOf<Task>()

            // get mandatory tasks
            val mandatoryTasks = tasks.filter {it.priority == Priority.MANDATORY}
            mandatoryTasks.shuffled()
            val currentDuration = Duration.ZERO

            // add mandatory tasks
            var index : Int = 0
            while (currentDuration < sessionDuration && index < mandatoryTasks.size) {
                pickedTasks.add(mandatoryTasks[index])
                currentDuration.plus(mandatoryTasks[index].duration)
                index ++

            }

            if (currentDuration < sessionDuration){
                // keep scheduling
            }


            // tasks -> scheduledTask
            val pickedScheduledTasks = taskToScheduledTask(pickedTasks,sessionDuration)

            return pickedScheduledTasks
        }



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