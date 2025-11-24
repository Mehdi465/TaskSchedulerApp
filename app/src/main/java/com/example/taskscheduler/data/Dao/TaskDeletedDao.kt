package com.example.taskscheduler.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.TaskDeleted
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDeletedDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskDeleted(taskDeleted: TaskDeleted): Long

    @Query("SELECT * FROM task_deleted")
    fun getAllTaskDeleted(): Flow<List<TaskDeleted>>

    @Query("SELECT * FROM task_deleted WHERE taskId = :taskId")
    fun getTaskDeletedById(taskId: Int): Flow<TaskDeleted?>

    @Query("DELETE FROM task_deleted")
    suspend fun clear()



}