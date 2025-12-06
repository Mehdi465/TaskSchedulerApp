package com.example.taskscheduler.data

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.taskscheduler.R
import java.util.Date
import kotlin.time.Duration
import java.time.Instant
import java.util.UUID
import java.time.Duration as JavaTimeDuration

data class ScheduledTask(
    val task: Task,
    val startTime: Date,
    val endTime: Date,
    val duration: Duration = getDurationFromDates(startTime,endTime),
    var isCompleted: Boolean = false,
    // unique id for drag
    val instanceId: String = UUID.randomUUID().toString(),
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

    // passing through argument
    fun isCurrentTask(currentDate:Date) : Boolean {
        return currentDate.after(startTime) && currentDate.before(endTime)
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

        fun taskToScheduledTask(tasks: List<Task>,startTime: Date,duration: Duration): List<ScheduledTask>{
            var result = mutableListOf<ScheduledTask>()
            var cumulDuration = Duration.ZERO

            for (task in tasks){
                var durationTask : Duration = task.duration
                if (cumulDuration + task.duration > duration){
                    //task.duration = duration - cumulDuration
                    durationTask = duration - cumulDuration
                }
                Log.d("task","${task.name} : ${durationTask}")

                // add new scheduled task
                result.add(
                    ScheduledTask(
                        task.copy(),
                        Date(startTime.time + cumulDuration.inWholeMilliseconds),
                        Date(startTime.time + durationTask.inWholeMilliseconds + cumulDuration.inWholeMilliseconds)
                    )
                )
                cumulDuration += task.duration
            }
            for (task in result){
                Log.d("task","${task.task.name} : ${task.duration}")
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
            var index = 0
            while (currentDuration < sessionDuration && index < mandatoryTasks.size) {
                pickedTasks.add(mandatoryTasks[index])
                currentDuration = addDuration(currentDuration,mandatoryTasks[index].duration)
                index ++
            }

            if (currentDuration < sessionDuration){

                 val extendedListTasks : MutableList<Task> = mutableListOf<Task>()
                 for (task in tasks){
                     when(task.priority){
                         Priority.LOW -> extendedListTasks.add(task)
                         Priority.MEDIUM -> {extendedListTasks.add(task)
                                            extendedListTasks.add(task)}
                         Priority.HIGH -> {extendedListTasks.add(task)
                                         extendedListTasks.add(task)
                                         extendedListTasks.add(task)}
                         else -> {}
                     }
                 }

                val extendedListSize = extendedListTasks.size -1

                while(currentDuration < sessionDuration){
                    val randomInt = (0..extendedListSize).random()
                    pickedTasks.add(extendedListTasks[randomInt])
                    currentDuration = addDuration(currentDuration,extendedListTasks[randomInt].duration)
                }
            }

            val pickedScheduledTasks = taskToScheduledTask(pickedTasks,startTime,sessionDuration)

            return pickedScheduledTasks
        }
    }

}