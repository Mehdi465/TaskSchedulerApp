package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    // helper functions
    @Insert
    suspend fun insertSession(session: Session): Long

    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    fun getSessionById(sessionId: Long): Flow<Session>

    @Query("SELECT * FROM sessions")
    fun getAllSessions(): Flow<List<Session>>

//    // The query to get everything back
//    @Transaction
//    @Query("SELECT * FROM sessions")
//    fun getAllFullSessionDetails(): Flow<List<FullSessionDetails>>
}