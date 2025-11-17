package com.example.taskscheduler.data

import com.example.taskscheduler.data.Session
import kotlinx.coroutines.flow.Flow

/**
 * Interface for the repository that handles operations related to user sessions.
 */
interface SessionRepository {

    /**
     * Inserts a complete session, including all of its scheduled tasks, into the data source.
     *
     * @param session The domain Session object to be saved.
     */
    suspend fun saveSession(session: Session): Long

    /**
     * Retrieves a single session with all its details by its ID.
     * The domain Session object will contain the list of ScheduledTasks.
     *
     * @param sessionId The ID of the session to retrieve.
     * @return A Flow emitting the Session, or null if not found.
     */
    fun getSession(sessionId: Long): Flow<Session?>

    /**
     * Retrieves all past sessions, ordered from newest to oldest.
     * Each domain Session object will contain its list of ScheduledTasks.
     *
     * @return A Flow emitting a list of all Sessions.
     */
    fun getPastSessions(): Flow<List<Session>>

    /**
     * Deletes a session and its associated task entries from the data source.
     *
     * @param sessionId The ID of the session to delete.
     */
    suspend fun deleteSession(sessionId: Long)
}