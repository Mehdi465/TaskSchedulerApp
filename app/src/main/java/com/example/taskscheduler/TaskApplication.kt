package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.AppContainer
import com.example.taskscheduler.data.AppDataContainer
import com.example.taskscheduler.data.OfflineTasksRepository
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskRepository
import kotlin.getValue
import com.example.taskscheduler.utils.NotificationUtils

class TaskApplication: Application() {
    lateinit var container: AppContainer


    private val database by lazy { TaskDatabase.getDatabase(this) }

    // Lazy initialization for the repository
    val tasksRepository: TaskRepository by lazy {
        OfflineTasksRepository(database.taskDao())
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