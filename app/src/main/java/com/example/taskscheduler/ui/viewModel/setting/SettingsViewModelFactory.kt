package com.example.taskscheduler.ui.viewModel.setting

import android.app.Application
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.AppContainer
import com.example.taskscheduler.data.AppDataContainer
import com.example.taskscheduler.data.SettingsRepository // Import
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.utils.NotificationUtils

class TaskApplication : Application() {
    lateinit var container: AppContainer

    private val database by lazy { TaskDatabase.getDatabase(this) }
    val tasksRepository: TaskRepository by lazy {
        com.example.taskscheduler.data.OfflineTasksRepository(
            database.taskDao(),
            database.taskTrackingDao()
        )
    }
    val activeSessionStore: ActiveSessionStore by lazy {
        ActiveSessionStore(applicationContext)
    }

    // Add SettingsRepository instance
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        NotificationUtils.createNotificationChannels(this)
    }
}