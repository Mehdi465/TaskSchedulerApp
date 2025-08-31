package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.taskscheduler.data.Converters.DateConverters
import java.util.Date

@Entity(
    tableName = "task_monitoring",
    foreignKeys = [
        ForeignKey(
            entity = Task::class, //parent table
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE, // If a Task is deleted, its monitoring data is also deleted
            onUpdate = ForeignKey.CASCADE // If Task id changes, update here
        )
    ]
)

@TypeConverters(DateConverters::class)
data class TaskMonitoring(
    @PrimaryKey
    val taskId: Long, // same id as Task

    var timesCompleted: Int = 0,
    var totalTimeMillisSpent: Long = 0,
    var usageDates: List<Date> = emptyList() // List of dates when the task was completed
)