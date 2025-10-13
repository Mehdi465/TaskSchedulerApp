package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Transaction
    suspend fun insertFullSession(session: Session, tasks: List<SessionTaskEntry>) {
        val sessionId = insertSession(session)
        val tasksWithSessionId = tasks.map { it.copy(sessionId = sessionId) }
        insertTaskEntries(tasksWithSessionId)
    }

    // Helper functions
    @Insert
    suspend fun insertSession(session: Session): Long
    @Insert
    suspend fun insertTaskEntries(tasks: List<SessionTaskEntry>)

    // The query to get everything back
    @Transaction
    @Query("SELECT * FROM sessions")
    fun getAllFullSessionDetails(): Flow<List<FullSessionDetails>>
}