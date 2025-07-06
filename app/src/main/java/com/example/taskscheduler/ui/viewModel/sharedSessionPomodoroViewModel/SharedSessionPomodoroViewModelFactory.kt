package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.data.ActiveSessionStore

class SharedSessionPomodoroViewModelFactory( private val activeSessionStore: ActiveSessionStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedSessionPomodoroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedSessionPomodoroViewModel(activeSessionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}