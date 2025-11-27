package com.example.taskscheduler.data.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskscheduler.data.Task
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

    /**
     * Retrieves a single task by its ID.
     * This is a suspend function for one-time queries.
     * Returns null if the task is not found.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskByIdOnce(taskId: Int): Task?

    // DELETE ALL
    @Query("DELETE FROM tasks")
    suspend fun clear()
}