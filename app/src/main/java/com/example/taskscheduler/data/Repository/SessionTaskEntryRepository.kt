package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.SessionTaskEntry
import kotlinx.coroutines.flow.Flow

interface SessionTaskEntryRepository {
    /**
     * insert sessionTaskEntry in the data source
     */
    suspend fun insertSessionTaskEntry(sessionTaskEntry: SessionTaskEntry): Long

    /**
     * retrive sessionTaskEntry from the data source
     */
    fun getSessionTaskEntry(id: Long): Flow<SessionTaskEntry>

    /**
     * retrive all sessionTaskEntry from the data source
     */
    fun getAllSessionTaskEntry(): Flow<List<SessionTaskEntry>>

    /**
     * retrive sessionTaskEntry from the data source for a given task
     */
    fun getSessionTaskEntryByTaskId(taskId: Int): Flow<SessionTaskEntry>

    /**
     * retrive sessionTaskEntry from the data source for a given session
     */
    fun getSessionTaskEntryBySessionId(sessionId: Long): Flow<SessionTaskEntry>
}