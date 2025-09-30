package com.example.taskscheduler.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val tasksRepository: TaskRepository
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
}