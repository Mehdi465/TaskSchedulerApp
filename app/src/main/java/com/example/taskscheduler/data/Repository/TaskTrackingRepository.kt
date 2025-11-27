package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.TaskTracking
import kotlinx.coroutines.flow.Flow

interface TaskTrackingRepository {
    /**
     * Retrieve all the tasks tracking from the the given data source.
     */
    fun getAllTasksTrackingStream(): Flow<List<TaskTracking>>

    /**
     * Retrieve an task tracking from the given data source that matches with the [id].
     */
    fun getTaskTrackingStream(id: Int): Flow<List<TaskTracking?>>

    /**
     * insert task tracking in the data source
     */
    suspend fun insertTaskTracking(task: TaskTracking): Long

    /**
     * Delete task tracking from the data source
     */
    suspend fun deleteTaskTracking(task: TaskTracking)

    /**
     * update task tracking in the data source
     */
    suspend fun updateTaskTracking(task: TaskTracking)

    /**
     * update date of a completed task in the data source
     */
    //suspend fun updateUsageDates(taskId: Long, newUsageDates: List<Date>)

    /**
     * Retrieve all the tasks from the the given data source.
     */
    fun getTaskTrackingById(id : Int): Flow<TaskTracking?>

    /**
     * update tracked task after a session is completed
     */
    suspend fun updateStatsAfterSession(
        taskId : Int,
        taskDurationMillis : Long,
    )

    /**
     * get the most completed task
     */
    fun getMostDoneTask() : Flow<TaskTracking?>

    suspend fun getTrackingForTaskOnce(taskId: Int): TaskTracking?

}