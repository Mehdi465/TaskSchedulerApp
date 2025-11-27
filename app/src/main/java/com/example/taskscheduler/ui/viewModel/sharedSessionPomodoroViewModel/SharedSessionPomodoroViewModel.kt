package com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.Repository.OfflineTaskDeletedRepository
import com.example.taskscheduler.data.ScheduledTask
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.Repository.SessionRepository
import com.example.taskscheduler.data.SessionTaskEntry
import com.example.taskscheduler.data.Repository.SessionTaskEntryRepository
import com.example.taskscheduler.data.Repository.TaskDeletedRepository
import com.example.taskscheduler.data.Repository.TaskRepository
import com.example.taskscheduler.data.Repository.TaskTrackingRepository
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

class SharedSessionPomodoroViewModel(
    private val activeSessionStore: ActiveSessionStore,
    private val taskTrackingRepository: TaskTrackingRepository,
    private val sessionRepository: SessionRepository,
    private val sessionTaskEntryRepository: SessionTaskEntryRepository,
    private val taskRepository: TaskRepository,
    private val taskDeletedRepository: TaskDeletedRepository
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

    /**
     * Called when the user validates the entire session.
     * It iterates through all tasks in the current session, updates their tracking
     * statistics in the database, and then clears the session.
     */
    fun onSessionValidated() {
        viewModelScope.launch {
            val currentSession = uiState.value.session
            if (currentSession == null || currentSession.scheduledTasks.isEmpty()) {
                Log.w("SharedSessionVM", "Validation called, but no active session or tasks found.")
                return@launch
            }

            val sessionEndTime = System.currentTimeMillis()

            // update session
            val sessionId = sessionRepository.saveSession(currentSession)
            Log.d("SharedSessionVM", "Updating session with ID: ${sessionId}")

            // go through each task in the session and update its stats
            for (taskInSession in currentSession.scheduledTasks) {
                // The duration for each task is stored in the ScheduledTask object itself
                val taskDurationMillis = taskInSession.duration.inWholeMilliseconds

                // update the task's stats in the database
                if (taskDurationMillis > 0) {
                    Log.d("SharedSessionVM", "Updating stats for Task ID: ${taskInSession.task.id} with duration: $taskDurationMillis ms")
                    val taskExists = taskRepository.getTaskByIdOnce(taskInSession.task.id) != null
                    if (!taskExists){

                        val deletedTask = taskDeletedRepository.getTaskDeletedByIdOnce(taskInSession.task.id)
                        if (deletedTask != null) {
                            val updatedDeletedTask =
                                    deletedTask.copy(
                                        timesCompleted = deletedTask.timesCompleted +1,
                                        totalTimeMillisSpent = deletedTask.totalTimeMillisSpent + taskDurationMillis
                                    )
                            taskDeletedRepository.upsertTask(updatedDeletedTask)
                            Log.d("SharedSession","Update deleted task ${updatedDeletedTask.id}")
                        }

                    }

                    else {
                        taskTrackingRepository.updateStatsAfterSession(
                            taskId = taskInSession.task.id,
                            taskDurationMillis = taskDurationMillis,
                        )

                        // update SessionTaskEntry
                        sessionTaskEntryRepository.insertSessionTaskEntry(
                            SessionTaskEntry(
                                sessionId = sessionId,
                                taskId = taskInSession.task.id,
                                startTime = taskInSession.startTime,
                                endTime = taskInSession.endTime,
                            )
                        )
                    }
                }
            }
            clearActiveSession()
        }
    }

}