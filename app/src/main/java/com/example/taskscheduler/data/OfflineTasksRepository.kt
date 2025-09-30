package com.example.taskscheduler.data

import kotlinx.coroutines.flow.Flow

class OfflineTasksRepository(
    private val taskDao: TaskDao,
    private val taskTrackingDao: TaskTrackingDao
): TaskRepository {
    override fun getAllTasksStream(): Flow<List<Task>> = taskDao.getAllTasks()

    override fun getTaskStream(id: Int): Flow<Task?> = taskDao.getTask(id)

    override suspend fun insertTask(task: Task):Long {
        val newTaskId = taskDao.insert(task) // Assuming insertTask returns the Long rowId/new ID
        if (newTaskId > 0) {
            val initialTrackingStats = TaskTracking(taskId = newTaskId.toInt())
            taskTrackingDao.insertTaskTracking(initialTrackingStats)
        }
        return newTaskId
    }

    // since there is cascade parameter set in Tasktracking
    override suspend fun deleteTask(task: Task) = taskDao.delete(task)

    override suspend fun updateTask(task: Task) = taskDao.update(task)

    override fun getTasksByIds(ids: List<Int>) = taskDao.getTasksByIds(ids)
}