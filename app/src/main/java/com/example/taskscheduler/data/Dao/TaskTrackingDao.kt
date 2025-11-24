package com.example.taskscheduler.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskscheduler.data.TaskTracking
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM task_Tracking ORDER BY timesCompleted DESC LIMIT 1")
    fun getMostDoneTask(): Flow<TaskTracking?>


    // DELETE ALL
    @Query("DELETE FROM task_Tracking")
    suspend fun clear()
}
