package com.example.taskscheduler.ui.viewModel.session

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.ActiveSessionStore
import com.example.taskscheduler.data.ScheduledTask.Companion.scheduleTasks
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date


data class SessionUiState(
    val loadedSelectedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val taskIdsString: String? = null
)

class SessionViewModel(
    application: Application,
    private val tasksRepository: TaskRepository,
    initialTaskIdsString: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var loadTasksJob: Job? = null // To manage the collection coroutine
    private val activeSessionStore = ActiveSessionStore(application.applicationContext)

    private val _isSavingSession = MutableStateFlow(false)
    val isSavingSession: StateFlow<Boolean> = _isSavingSession.asStateFlow()

    private val _sessionSaveCompleteAndNavigate = MutableStateFlow<Boolean>(false)
    val sessionSaveCompleteAndNavigate: StateFlow<Boolean> = _sessionSaveCompleteAndNavigate.asStateFlow()

    private val _saveErrorMessage = MutableStateFlow<String?>(null)
    val saveErrorMessage: StateFlow<String?> = _saveErrorMessage.asStateFlow()

    var taskIdsString: String? = null

    init {
        val taskIdsString: String? = initialTaskIdsString
        if (!taskIdsString.isNullOrBlank()) {
            val ids = taskIdsString.split(",").mapNotNull { it.toIntOrNull() }
            if (ids.isNotEmpty()) {
                loadSelectedTasks(ids)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No valid task IDs provided.") }
            }
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "No tasks selected.") }
        }
    }

    private fun loadSelectedTasks(ids: List<Int>) {
        // Cancel any previous loading job to avoid multiple collections if this function is called again
        loadTasksJob?.cancel()

        loadTasksJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // Set loading and clear previous error

            tasksRepository.getTasksByIds(ids) // This returns Flow<List<Task>>
                .catch { exception -> // Handle errors from the Flow itself
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error loading selected tasks: ${exception.message}"
                        )
                    }
                }
                .collect { selectedTasks -> // Collect emissions from the Flow
                    _uiState.update {
                        it.copy(
                            loadedSelectedTasks = selectedTasks,
                            isLoading = false // Data loaded, set loading to false
                        )
                    }
                }
        }
    }

    fun onConfirmAndSaveSession(
        sessionStartTime: Date,
        sessionEndTime: Date,
        tasks: List<Task>
    ) {
        viewModelScope.launch {
            _isSavingSession.value = true
            _saveErrorMessage.value = null
            try {

                val scheduledTask = scheduleTasks(tasks,sessionStartTime,sessionEndTime)
                val newSession = Session(
                    scheduledTasks = scheduledTask,
                    startTime = sessionStartTime,
                    endTime = sessionEndTime
                )

                activeSessionStore.saveActiveSession(newSession)
                _uiState.value =
                    SessionUiState(isLoading = false)

                _sessionSaveCompleteAndNavigate.value = true // Signal UI to navigate

            } catch (e: Exception) {
                Log.e("SessionViewModel", "Error saving session", e)
                _saveErrorMessage.value = "Error: ${e.message}"
            } finally {
                _isSavingSession.value = false
            }
        }
    }

    fun onNavigationToScheduleHomeComplete() {
        _sessionSaveCompleteAndNavigate.value = false
    }

    fun clearSaveErrorMessage() {
        _saveErrorMessage.value = null
    }

    // TODO: cancel the job if the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        loadTasksJob?.cancel()
    }


}