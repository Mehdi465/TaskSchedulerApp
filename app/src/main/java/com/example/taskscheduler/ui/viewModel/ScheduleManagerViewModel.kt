package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.Session
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import android.util.Log // For logging

// Data class for ScheduleScreen UI State
data class ScheduleUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
    val errorMessage: String? = null
)

class ScheduleViewModel(
    private val activeSessionStore: ActiveSessionStore
) : ViewModel() {

    val uiState: StateFlow<ScheduleUiState> = activeSessionStore.activeSessionFlow
        .map { session ->
            if (session != null) {
                Log.d("ScheduleViewModel", "Session loaded: ${session.scheduledTasks.size} tasks")
                ScheduleUiState(isLoading = false, session = session)
            } else {
                Log.d("ScheduleViewModel", "No active session found.")
                ScheduleUiState(
                    isLoading = false,
                    session = null,
                    errorMessage = "No active schedule found."
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ScheduleUiState(isLoading = true) // Start with loading state
        )

    // When a session ends
    fun clearSession(){

    }
}