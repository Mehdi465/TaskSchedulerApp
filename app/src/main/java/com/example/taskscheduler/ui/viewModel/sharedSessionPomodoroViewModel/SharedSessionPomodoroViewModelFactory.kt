package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.OfflineTaskTrackingRepository
import com.example.taskscheduler.data.TaskTrackingRepository

class SharedSessionPomodoroViewModelFactory(
    private val activeSessionStore: ActiveSessionStore,
    private val taskTrackingRepository : TaskTrackingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedSessionPomodoroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedSessionPomodoroViewModel(activeSessionStore,taskTrackingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}