package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.ScheduledTask
import com.example.taskscheduler.data.Session
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class SharedSessionPomodoroUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
    val errorMessage: String? = null,
    val currentTask: ScheduledTask? = null
)

class SharedSessionPomodoroViewModel(private val activeSessionStore: ActiveSessionStore
) : ViewModel() {

    val uiState: StateFlow<SharedSessionPomodoroUiState> = activeSessionStore.activeSessionFlow
        .map { session ->
            if (session != null) {
                SharedSessionPomodoroUiState(
                    isLoading = false,
                    session = session,
                    currentTask = session.getCurrentTask()
                )
            } else {
                SharedSessionPomodoroUiState(
                    isLoading = false,
                    session = null,
                    errorMessage = "No active schedule found.",
                    currentTask = null
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SharedSessionPomodoroUiState(isLoading = true) // Start with loading state
        )

    // When a session ends
    fun clearActiveSession(){
        viewModelScope.launch {
            activeSessionStore.clearActiveSession()
        }
    }
}