package com.example.taskscheduler.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

class OfflineTaskTrackingRepository(private val taskTrackingDao: TaskTrackingDao) : TaskTrackingRepository {
    override fun getAllTasksMonitoringStream(): Flow<List<TaskTracking>> = taskTrackingDao.getAllTaskTracking()

    override fun getTaskMonitoringStream(id: Int): Flow<TaskTracking?> = taskTrackingDao.

    override suspend fun insertTaskMonitoring(task: TaskTracking): Long = taskTrackingDao.insertTaskTracking(task)

    override suspend fun deleteTaskMonitoring(task: TaskTracking) = taskTrackingDao.deleteTaskMonitoring(task)

    override suspend fun updateTaskMonitoring(task: TaskTracking) = taskTrackingDao.updateTaskTracking(task)

    //override suspend fun updateUsageDates(taskId: Long, newUsageDates: List<Date>)

    override fun getTasksMonitoringByIds(ids: List<Int>): Flow<List<TaskTracking>> = taskTrackingDao.getTasksMonitoringByIds(ids)
}