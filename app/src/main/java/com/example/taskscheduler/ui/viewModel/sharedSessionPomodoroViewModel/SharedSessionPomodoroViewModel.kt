package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.Session
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


data class SharedSessionPomodoroUiState(
    val isLoading: Boolean = true,
    val session: Session? = null,
    val errorMessage: String? = null
)

class SharedSessionPomodoroViewModel(private val activeSessionStore: ActiveSessionStore
) : ViewModel() {

    val uiState: StateFlow<SharedSessionPomodoroUiState> = activeSessionStore.activeSessionFlow


        .map { session ->
            if (session != null) {
                SharedSessionPomodoroUiState(isLoading = false, session = session)
            } else {
                SharedSessionPomodoroUiState(
                    isLoading = false,
                    session = null,
                    errorMessage = "No active schedule found."
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = SharedSessionPomodoroUiState(isLoading = true) // Start with loading state
        )

    // When a session ends
    fun clearSession(){

    }
}