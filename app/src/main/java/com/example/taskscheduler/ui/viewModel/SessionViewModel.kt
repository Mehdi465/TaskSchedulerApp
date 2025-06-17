package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.ui.SessionDestination
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class SessionUiState(
    val loadedSelectedTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class SessionViewModel(
    private val tasksRepository: TaskRepository,
    savedStateHandle: SavedStateHandle // To access navigation arguments
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    private var loadTasksJob: Job? = null // To manage the collection coroutine

    init {
        val taskIdsString: String? = savedStateHandle[SessionDestination.SELECTED_TASK_IDS_ARG]
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

    // Optional: It's good practice to cancel the job if the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        loadTasksJob?.cancel()
    }
}