package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionTaskEntryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSessionTaskEntry(sessionTaskEntry: SessionTaskEntry): Long

    @Query("SELECT * FROM sessions_task_entry WHERE entryId = :id")
    fun getSessionTaskEntry(id: Long): Flow<SessionTaskEntry>

    @Query("SELECT * FROM sessions_task_entry")
    fun getAllSessionTaskEntry(): Flow<List<SessionTaskEntry>>

    @Query("SELECT * FROM sessions_task_entry WHERE taskId = :taskId")
    fun getSessionTaskEntryByTaskId(taskId: Int): Flow<SessionTaskEntry>

    @Query("SELECT * FROM sessions_task_entry WHERE sessionId = :sessionId")
    fun getSessionTaskEntryBySessionId(sessionId: Long): Flow<SessionTaskEntry>
}