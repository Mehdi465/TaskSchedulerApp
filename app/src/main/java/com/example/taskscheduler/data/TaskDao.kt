package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    // For the task manager list
    @Query("SELECT * from tasks ORDER BY id ASC")
    fun getAllTasks() : Flow<List<Task>>

    // For the task session list
    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int) : Flow<Task?>

    @Query("SELECT * FROM tasks WHERE id IN (:taskIds)")
    fun getTasksByIds(taskIds: List<Int>): Flow<List<Task>>

}