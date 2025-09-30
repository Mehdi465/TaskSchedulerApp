package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskTrackingDao {

    @Query("SELECT * FROM task_Tracking")
    fun getAllTaskTracking(): Flow<List<TaskTracking>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskTracking(taskTracking: TaskTracking): Long

    @Update
    suspend fun updateTaskTracking(taskTracking: TaskTracking)

    @Query("UPDATE task_Tracking SET timesCompleted = timesCompleted + 1, totalTimeMillisSpent = totalTimeMillisSpent + :timeSpent WHERE taskId = :taskId")
    suspend fun incrementCompletionAndLogTime(taskId: Long, timeSpent: Long)

    @Query("DELETE FROM task_Tracking WHERE taskId = :taskId")
    suspend fun deleteTaskTrackingById(taskId: Long)

    @Query("SELECT * FROM task_Tracking WHERE taskId = :taskId")
    fun getTaskTrackingById(taskId: Int): Flow<TaskTracking?>
}