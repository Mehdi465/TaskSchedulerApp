package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.AppContainer
import com.example.taskscheduler.data.AppDataContainer
import com.example.taskscheduler.data.OfflineTasksRepository
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskRepository
import kotlin.getValue

class TaskApplication: Application() {
    lateinit var container: AppContainer

    private val database by lazy { TaskDatabase.getDatabase(this) }

    // Lazy initialization for the repository
    // It depends on the DAO, which comes from the database
    val tasksRepository: TaskRepository by lazy {
        OfflineTasksRepository(database.taskDao())
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}