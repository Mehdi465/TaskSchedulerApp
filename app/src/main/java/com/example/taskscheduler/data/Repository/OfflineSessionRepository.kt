package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.Dao.SessionDao
import com.example.taskscheduler.data.Dao.TaskTrackingDao
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.temporal.ChronoUnit

class OfflineSessionRepository(
    private val sessionDao: SessionDao,
    private val taskTrackingDao: TaskTrackingDao
) : SessionRepository {
    override suspend fun deleteSession(sessionId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun saveSession(session: Session): Long {
        return sessionDao.insertSession(session)
    }

    override fun getSession(sessionId: Long): Flow<Session?> = sessionDao.getSessionById(sessionId)

    override fun getPastSessions(): Flow<List<Session>> = sessionDao.getAllSessions()

    override fun getLastWeekSessions():Flow<List<Session>>{
        val now = Instant.now()
        val oneWeekAgo = now.minus(7, ChronoUnit.DAYS)
        val oneWeekAgoTimestamp = oneWeekAgo.toEpochMilli()

        return sessionDao.getSessionsSince(oneWeekAgoTimestamp)
    }

    override fun getFirstSession(): Flow<Session?> = sessionDao.getFirstSession()

    override fun getLastSession(): Flow<Session?> = sessionDao.getLastSession()
}