package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.AppContainer
import com.example.taskscheduler.data.AppDataContainer
import com.example.taskscheduler.data.Repository.OfflineSessionRepository
import com.example.taskscheduler.data.Repository.OfflineSessionTaskEntryRepository
import com.example.taskscheduler.data.Repository.OfflineTaskDeletedRepository
import com.example.taskscheduler.data.Repository.OfflineTaskTrackingRepository
import com.example.taskscheduler.data.Repository.OfflineTasksRepository
import com.example.taskscheduler.data.Repository.SessionRepository
import com.example.taskscheduler.data.Repository.SessionTaskEntryRepository
import com.example.taskscheduler.data.Repository.TaskDeletedRepository
import com.example.taskscheduler.data.SettingsRepository
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.Repository.TaskRepository
import com.example.taskscheduler.data.Repository.TaskTrackingRepository
import kotlin.getValue
import com.example.taskscheduler.utils.NotificationUtils

class TaskApplication: Application() {
    lateinit var container: AppContainer

    private val database by lazy { TaskDatabase.getDatabase(this) }

    // Lazy initialization for the repository
    val tasksRepository: TaskRepository by lazy {
        OfflineTasksRepository(
            database.taskDao(),
            database.taskTrackingDao(),
            database.sessionDao(),
            database.sessionTaskEntryDao(),
            database.taskDeletedDao()
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(applicationContext)
    }

    val sessionRepository: SessionRepository by lazy {
        OfflineSessionRepository(
            database.sessionDao(),
            database.taskTrackingDao()
        )
    }

    val sessionTaskEntryRepository : SessionTaskEntryRepository by lazy {
        OfflineSessionTaskEntryRepository(
            database.sessionTaskEntryDao()
        )
    }

    val taskTrackingRepository : TaskTrackingRepository by lazy {
        OfflineTaskTrackingRepository(
            database.taskTrackingDao()
        )
    }

    val taskDeletedRepository : TaskDeletedRepository by lazy {
        OfflineTaskDeletedRepository(
            database.taskDeletedDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        NotificationUtils.createNotificationChannels(this)
    }

    val activeSessionStore: ActiveSessionStore by lazy {
        ActiveSessionStore(applicationContext.applicationContext)
    }
}