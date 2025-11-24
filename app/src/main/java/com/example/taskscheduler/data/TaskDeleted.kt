package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_deleted")
data class TaskDeleted(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskId: Int,
    val name: String,
    val priority: String,
    val icon: String,
    val color: String,
    val timesCompleted: Int,
    val totalTimeMillisSpent: Long
)