package com.example.taskscheduler.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Date
import kotlin.text.map
import kotlin.text.toInt
import kotlin.time.Duration.Companion.milliseconds

// --- To Persistence Models (Domain -> Persistence) ---

fun Task.toPersistence(): TaskPersistence {
    return TaskPersistence(
        id = this.id,
        name = this.name,
        priorityName = this.priority.name,
        durationMillis = this.duration.inWholeMilliseconds,
        iconResId = this.icon,
        colorArgb = this.color.toArgb().toLong()
    )
}

fun ScheduledTask.toPersistence(): ScheduledTaskPersistence {
    return ScheduledTaskPersistence(
        task = this.task.toPersistence(),
        startTimeMillis = this.startTime.time,
        endTimeMillis = this.endTime.time
    )
}

fun Session.toPersistence(): SessionPersistence {
    return SessionPersistence(
        sessionStartTimeMillis = this.startTime.time,
        sessionEndTimeMillis = this.endTime.time,
        scheduledTasks = this.scheduledTasks.map { it.toPersistence() }
    )
}

// --- From Persistence Models (Persistence -> Domain) ---
// (Needed if you plan to read the session back from DataStore)

fun TaskPersistence.toDomain(): Task {
    return Task(
        id = this.id,
        name = this.name,
        priority = try { Priority.valueOf(this.priorityName) } catch (e: IllegalArgumentException) { Priority.MEDIUM /* Provide a sensible default */ },
        duration = this.durationMillis.milliseconds,
        icon = this.iconResId,
        color =  Color(this.colorArgb.toInt())
    )
}

fun ScheduledTaskPersistence.toDomain(): ScheduledTask {
    return ScheduledTask(
        task = this.task.toDomain(),
        startTime = Date(this.startTimeMillis),
        endTime = Date(this.endTimeMillis)
    )
}

fun SessionPersistence.toDomain(): Session {
    return Session(
        startTime = Date(this.sessionStartTimeMillis),
        endTime = Date(this.sessionEndTimeMillis),
        scheduledTasks = this.scheduledTasks.map { it.toDomain() }
    )
}