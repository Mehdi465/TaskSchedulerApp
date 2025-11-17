package com.example.taskscheduler.data

import android.util.Log
import androidx.compose.animation.core.copy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OfflineTaskTrackingRepository(private val taskTrackingDao: TaskTrackingDao) : TaskTrackingRepository {

    override fun getTaskTrackingStream(id: Int): Flow<List<TaskTracking?>> = taskTrackingDao.getAllTaskTracking()

    override fun getAllTasksTrackingStream(): Flow<List<TaskTracking>> = taskTrackingDao.getAllTaskTracking()

    override suspend fun insertTaskTracking(task: TaskTracking): Long = taskTrackingDao.insertTaskTracking(task)

    override suspend fun deleteTaskTracking(task: TaskTracking) = taskTrackingDao.updateTaskTracking(task)

    override suspend fun updateTaskTracking(task: TaskTracking) = taskTrackingDao.updateTaskTracking(task)

    override fun getTaskTrackingById(id: Int): Flow<TaskTracking?> = taskTrackingDao.getTaskTrackingById(id)

    // Logical part
    override suspend fun updateStatsAfterSession(
        taskId: Int,
        taskDurationMillis: Long,
    ) {
        val currentStats = taskTrackingDao.getTaskTrackingById(taskId).firstOrNull()
        if (currentStats != null) {
            val newTotalTime = currentStats.totalTimeMillisSpent + taskDurationMillis
            val newSessionsCompleted = currentStats.timesCompleted + 1
            Log.d("TaskTrackingRepo", " count : $newSessionsCompleted")
            taskTrackingDao.updateTaskTracking(
                currentStats.copy(
                       timesCompleted = newSessionsCompleted,
                       totalTimeMillisSpent =  newTotalTime
                )
            )
        } else {
            //if stats don't exist, create them with this first session's data
            val newStats = TaskTracking(
                taskId = taskId,
                totalTimeMillisSpent = taskDurationMillis,
                timesCompleted = 1,
            )
            Log.d("TaskTrackingRepo", "new task tracking : $taskId, count : ${newStats.timesCompleted}")
            taskTrackingDao.insertTaskTracking(newStats)
        }
    }
}