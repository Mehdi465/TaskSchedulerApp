package com.example.taskscheduler.data

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.taskscheduler.R
import java.util.Date
import kotlin.time.Duration
import java.time.Instant
import java.time.Duration as JavaTimeDuration

data class ScheduledTask(
    val task: Task,
    val startTime: Date,
    val endTime: Date,
    var isCompleted: Boolean = false
){
    fun isCurrentlyActive(): Boolean{
        val currentDate = Date()
        return currentDate.after(startTime) && currentDate.before(endTime)
    }

    val name: String
        get() = task.name

    fun getDurationInTime(): String{
        val duration = endTime.time - startTime.time
        val hours = duration / (1000*60*60)
        val minutes = (duration % (1000*60*60)) / (1000*60)
        return "$hours h : $minutes m"
    }

    companion object{
        fun addDuration(duration1: Duration, duration2: Duration): Duration {
            return duration1 + duration2
        }

        fun getDurationFromDates(startTime: Date, endTime: Date): Duration {
            val javaSessionDuration : JavaTimeDuration = JavaTimeDuration.between(
                startTime.toInstant(),endTime.toInstant())
            return Duration.parse(javaSessionDuration.toString())
        }

        fun taskToScheduledTask(tasks: List<Task>,duration: Duration): List<ScheduledTask>{
            var result = mutableListOf<ScheduledTask>()
            var cumulDuration = Duration.ZERO

            // no reducing task duration // TODO: make the last task duration flexible
            for (task in tasks){
                val scheduledTask = ScheduledTask(
                    task,Date(System.currentTimeMillis() + cumulDuration.inWholeMilliseconds),
                    Date(System.currentTimeMillis() + task.duration.inWholeMilliseconds + cumulDuration.inWholeMilliseconds))
                result.add(scheduledTask)
                cumulDuration += task.duration
            }
            return result
        }

        fun scheduleTasks(tasks:List<Task>,startTime:Date,endTime:Date):List<ScheduledTask>{

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
            var currentDuration = Duration.ZERO

            // add mandatory tasks
            var index : Int = 0
            while (currentDuration < sessionDuration && index < mandatoryTasks.size) {
                pickedTasks.add(mandatoryTasks[index])
                currentDuration = addDuration(currentDuration,mandatoryTasks[index].duration)
                index ++
            }

            if (currentDuration < sessionDuration){
                // keep scheduling
                // get high, medium and low tasks
                val highPriorityTasks = tasks.filter {it.priority == Priority.HIGH}
                val mediumPriorityTasks = tasks.filter {it.priority == Priority.MEDIUM}
                val lowPriorityTasks = tasks.filter {it.priority == Priority.LOW}

                // do not fall in forever loop
                val isAllEmpty : Boolean = highPriorityTasks.isEmpty() && mediumPriorityTasks.isEmpty() && lowPriorityTasks.isEmpty()

                while(currentDuration < sessionDuration && !isAllEmpty){
                    val randomInt = (0..5).random()
                    var pickedTask : Task = Task.DEFAULT_TASK

                    // low tasks
                    if (randomInt == 0 && !lowPriorityTasks.isEmpty()){
                        pickedTask = lowPriorityTasks.random()
                        pickedTasks.add(pickedTask)
                        currentDuration = addDuration(currentDuration,pickedTask.duration)
                    }
                    // medium tasks
                    else if (randomInt < 3 && !mediumPriorityTasks.isEmpty()){
                        pickedTask = mediumPriorityTasks.random()
                        pickedTasks.add(pickedTask)
                        currentDuration = addDuration(currentDuration,pickedTask.duration)
                    }
                    // high tasks
                    else{
                        if (!highPriorityTasks.isEmpty()) {
                            pickedTask = highPriorityTasks.random()
                            pickedTasks.add(pickedTask)
                            currentDuration = addDuration(currentDuration,pickedTask.duration)
                        }
                    }
                }
            }

            val pickedScheduledTasks = taskToScheduledTask(pickedTasks,sessionDuration)

            return pickedScheduledTasks
        }
    }

}