package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.Dao.TaskDeletedDao
import com.example.taskscheduler.data.TaskDeleted
import kotlinx.coroutines.flow.Flow

class OfflineTaskDeletedRepository(
    private val taskDeletedDao: TaskDeletedDao
): TaskDeletedRepository {

    override suspend fun insertTaskDeleted(taskDeleted: TaskDeleted): Long
        = taskDeletedDao.insertTaskDeleted(taskDeleted)

    override fun getAllTaskDeleted(): Flow<List<TaskDeleted>>
        = taskDeletedDao.getAllTaskDeleted()

    override suspend fun upsertTask(taskDeleted: TaskDeleted)
        = taskDeletedDao.upsertTask(taskDeleted)

    override fun getTaskDeletedById(taskId: Int): Flow<TaskDeleted?>
        = taskDeletedDao.getTaskDeletedById(taskId)

    override suspend fun getTaskDeletedByIdOnce(taskId: Int): TaskDeleted?
            = taskDeletedDao.getTaskDeletedByIdOnce(taskId)

    override suspend fun clear() = taskDeletedDao.clear()
}