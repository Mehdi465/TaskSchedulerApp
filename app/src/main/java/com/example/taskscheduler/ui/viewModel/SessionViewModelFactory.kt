package com.example.taskscheduler.ui.viewModel

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.example.taskscheduler.data.TaskRepository

class SessionViewModelFactory(
    private val application: Application,
    private val taskRepository: TaskRepository,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    private val initialTaskIdsString: String?
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(application,taskRepository, initialTaskIdsString) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}