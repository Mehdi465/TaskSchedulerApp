package com.example.taskscheduler.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val tasksRepository: TaskRepository
    val sessionRepository: SessionRepository
    val taskTrackingRepository: TaskTrackingRepository
    val sessionTaskEntryRepository: SessionTaskEntryRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineTasksRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [TasksRepository]
     */
    override val tasksRepository: TaskRepository by lazy {
        OfflineTasksRepository(
            TaskDatabase.getDatabase(context).taskDao(),
            TaskDatabase.getDatabase(context).taskTrackingDao()
        )
    }

    override val sessionRepository: SessionRepository by lazy {
        OfflineSessionRepository(
            TaskDatabase.getDatabase(context).sessionDao(),
            TaskDatabase.getDatabase(context).taskTrackingDao(),
        )
    }

    override val taskTrackingRepository: TaskTrackingRepository by lazy {
        OfflineTaskTrackingRepository(
            TaskDatabase.getDatabase(context).taskTrackingDao(),
        )
    }

    override val sessionTaskEntryRepository: SessionTaskEntryRepository by lazy {
        OfflineSessionTaskEntryRepository(
            TaskDatabase.getDatabase(context).sessionTaskEntryDao(),
        )
    }
}