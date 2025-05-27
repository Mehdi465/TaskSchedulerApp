package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.AppContainer
import com.example.taskscheduler.data.AppDataContainer

class TaskApplication: Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}