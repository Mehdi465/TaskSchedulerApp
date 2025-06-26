package com.example.taskscheduler.data

import kotlinx.serialization.Serializable

@Serializable
data class TaskPersistence(
    val id: Int,
    val name: String,
    val priorityName: String,
    val durationMillis: Long,
    val iconResName: String?,
    val colorArgb: Long
)

@Serializable
data class ScheduledTaskPersistence(
    val task: TaskPersistence,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    var isCompleted: Boolean = false
)

@Serializable
data class SessionPersistence(
    val sessionStartTimeMillis: Long,
    val sessionEndTimeMillis: Long,
    val scheduledTasks: List<ScheduledTaskPersistence>
)