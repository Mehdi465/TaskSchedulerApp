package com.example.taskscheduler.data.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.taskscheduler.data.Session
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

    @Query("SELECT * FROM sessions WHERE startTime >= :timestamp ORDER BY startTime DESC")
    fun getSessionsSince(timestamp: Long): Flow<List<Session>>

    @Query("SELECT * FROM sessions ORDER BY startTime DESC LIMIT 1")
    fun getFirstSession():Flow<Session?>

    @Query("SELECT * FROM sessions ORDER BY startTime ASC LIMIT 1")
    fun getLastSession():Flow<Session?>

//    // The query to get everything back
//    @Transaction
//    @Query("SELECT * FROM sessions")
//    fun getAllFullSessionDetails(): Flow<List<FullSessionDetails>>

    // DELETE ALL
    @Query("DELETE FROM sessions")
    suspend fun clear()
}