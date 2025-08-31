package com.example.taskscheduler.data

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TaskMonitoringRepository {
    /**
     * Retrieve all the tasks monitoring from the the given data source.
     */
    fun getAllTasksMonitoringStream(): Flow<List<Task>>

    /**
     * Retrieve an task monitoring from the given data source that matches with the [id].
     */
    fun getTaskMonitoringStream(id: Int): Flow<Task?>

    /**
     * Insert task monitoring in the data source
     */
    suspend fun insertTaskMonitoring(task: Task): Long

    /**
     * Delete task monitoring from the data source
     */
    suspend fun deleteTaskMonitoring(task: Task)

    /**
     * Update task monitoring in the data source
     */
    suspend fun updateTaskMonitoring(task: Task)

    /**
     * Update date of a completed task in the data source
     */
    suspend fun updateUsageDates(taskId: Long, newUsageDates: List<Date>)

    /**
     * Retrieve all the tasks from the the given data source.
     */
    fun getTasksMonitoringByIds(ids: List<Int>): Flow<List<Task>>
}