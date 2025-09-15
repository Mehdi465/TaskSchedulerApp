package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import android.util.Log
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
import java.util.Calendar
import java.util.Date


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

    fun reorderScheduledTasks(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentSession = uiState.value.session ?: return@launch

            if (fromIndex == toIndex) return@launch // No change

            val currentTasks = currentSession.scheduledTasks.toMutableList()
            val movedItem = currentTasks.removeAt(fromIndex)

            // Adjust 'toIndex' if item is moved downwards past its original position
            val actualToIndex = if (toIndex > fromIndex) toIndex -1 else toIndex
            currentTasks.add(actualToIndex, movedItem)


            val reorderedSession = currentSession.copy(
                scheduledTasks = recalculateTaskTimes(currentTasks, currentSession.startTime)
            )
            // Update the session in your store
            activeSessionStore.updateActiveSession(reorderedSession) // You'll need this method in ActiveSessionStore
        }
    }

    /**
     * Recalculates start and end times for a list of tasks based on a session start time.
     * This is crucial after reordering.
     */
    private fun recalculateTaskTimes(
        tasks: List<ScheduledTask>,
        sessionStartTime: Date
    ): List<ScheduledTask> {
        val updatedTasks = mutableListOf<ScheduledTask>()
        var currentTaskStartTime = sessionStartTime
        tasks.forEach { originalScheduledTask ->
            val taskDurationMillis = originalScheduledTask.duration.inWholeMilliseconds

            val calendarStart = Calendar.getInstance().apply { time = currentTaskStartTime }
            val calendarEnd = Calendar.getInstance().apply {
                time = currentTaskStartTime
                add(Calendar.MILLISECOND, taskDurationMillis.toInt())
            }

            updatedTasks.add(
                originalScheduledTask.copy(
                    startTime = calendarStart.time,
                    endTime = calendarEnd.time
                    // Ensure other relevant properties are copied
                )
            )
            currentTaskStartTime = calendarEnd.time // Next task starts when this one ends
        }
        return updatedTasks.toList()
    }

    // When a session ends
    fun clearActiveSession(){
        viewModelScope.launch {
            activeSessionStore.clearActiveSession()
        }
    }
}