package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.TaskDeleted
import kotlinx.coroutines.flow.Flow

interface TaskDeletedRepository {
    suspend fun insertTaskDeleted(taskDeleted: TaskDeleted): Long

    fun getAllTaskDeleted(): Flow<List<TaskDeleted>>

    fun getTaskDeletedById(taskId: Int): Flow<TaskDeleted?>

    suspend fun getTaskDeletedByIdOnce(taskId: Int): TaskDeleted?

    suspend fun upsertTask(taskDeleted: TaskDeleted)

    suspend fun clear()
}
