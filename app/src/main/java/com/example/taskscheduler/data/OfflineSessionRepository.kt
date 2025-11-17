package com.example.taskscheduler.data

import kotlinx.coroutines.flow.Flow

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
}