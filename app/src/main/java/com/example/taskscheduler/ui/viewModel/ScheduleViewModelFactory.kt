package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.data.ActiveSessionStore

class ScheduleViewModelFactory(
    private val activeSessionStore: ActiveSessionStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ScheduleViewModel(activeSessionStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}