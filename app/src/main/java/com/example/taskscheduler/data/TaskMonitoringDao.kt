package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskMonitoringDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskMonitoring(taskMonitoring: TaskMonitoring)

    @Update
    suspend fun updateTaskMonitoring(taskMonitoring: TaskMonitoring)

    @Query("SELECT * FROM task_monitoring WHERE taskId = :taskId")
    fun getTaskMonitoringById(taskId: Long): Flow<TaskMonitoring?>

    @Query("SELECT * FROM task_monitoring")
    fun getAllTaskMonitoring(): Flow<List<TaskMonitoring>>

    @Query("UPDATE task_monitoring SET timesCompleted = timesCompleted + 1, usageDates = :newUsageDates, totalTimeMillisSpent = totalTimeMillisSpent + :timeSpent WHERE taskId = :taskId")
    suspend fun incrementCompletionAndLogTime(taskId: Long, timeSpent: Long, newUsageDates: List<Date>)

    @Query("UPDATE task_monitoring SET usageDates = :newUsageDates WHERE taskId = :taskId")
    suspend fun updateUsageDates(taskId: Long, newUsageDates: List<Date>)

    @Query("DELETE FROM task_monitoring WHERE taskId = :taskId")
    suspend fun deleteTaskMonitoringById(taskId: Long)
}