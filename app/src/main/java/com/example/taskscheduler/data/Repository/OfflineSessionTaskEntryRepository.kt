package com.example.taskscheduler.data.Repository

import com.example.taskscheduler.data.SessionTaskEntry
import com.example.taskscheduler.data.Dao.SessionTaskEntryDao

class OfflineSessionTaskEntryRepository(
    private val sessionTaskEntryDao: SessionTaskEntryDao
): SessionTaskEntryRepository{
    override suspend fun insertSessionTaskEntry(sessionTaskEntry: SessionTaskEntry) =
        sessionTaskEntryDao.insertSessionTaskEntry(sessionTaskEntry)


    override fun getSessionTaskEntry(id: Long) = sessionTaskEntryDao.getSessionTaskEntry(id)

    /**
     * retrive all sessionTaskEntry from the data source
     */
    override fun getAllSessionTaskEntry() = sessionTaskEntryDao.getAllSessionTaskEntry()

    /**
     * retrive sessionTaskEntry from the data source for a given task
     */
    override fun getSessionTaskEntryByTaskId(taskId: Int)
    = sessionTaskEntryDao.getSessionTaskEntryByTaskId(taskId)

    /**
     * retrive sessionTaskEntry from the data source for a given session
     */
    override fun getSessionTaskEntryBySessionId(sessionId: Long)
    = sessionTaskEntryDao.getSessionTaskEntryBySessionId(sessionId)
}