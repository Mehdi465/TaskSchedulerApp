package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.Dao.SessionDao
import com.example.taskscheduler.data.Dao.SessionTaskEntryDao
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.Dao.TaskDao
import com.example.taskscheduler.data.Dao.TaskDeletedDao
import com.example.taskscheduler.data.TaskTracking
import com.example.taskscheduler.data.Dao.TaskTrackingDao
import kotlinx.coroutines.flow.Flow

class OfflineTasksRepository(
    private val taskDao: TaskDao,
    private val taskTrackingDao: TaskTrackingDao,
    private val sessionDao: SessionDao,
    private val sessionTaskEntryDao: SessionTaskEntryDao,
    private val taskDeletedDao: TaskDeletedDao
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

    override suspend fun getTaskByIdOnce(taskId: Int): Task? {
        return taskDao.getTaskByIdOnce(taskId)
    }

    override suspend fun clearAllData(){
        taskTrackingDao.clear()
        taskDao.clear()
        sessionTaskEntryDao.clear()
        sessionDao.clear()
        taskDeletedDao.clear()
    }
}