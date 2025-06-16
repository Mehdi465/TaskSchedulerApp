package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Priority
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the Task List screen
 */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null // Optional: for displaying errors to the user
)

/**
 * ViewModel to retrieve and manage task data for the TaskManagerPage.
 *
 * @param tasksRepository The repository for accessing task data.
 */
class TaskManagerViewModel(
    private val tasksRepository: TaskRepository // Injected via TaskViewModelFactory
) : ViewModel() {

    /**
     * Holds the UI state for the task list.
     * This StateFlow is observed by the UI to display tasks and loading states.
     */
    val taskListUiState: StateFlow<TaskListUiState> =
        tasksRepository.getAllTasksStream() // Assuming this returns Flow<List<Task>>
            .map { tasks ->
                TaskListUiState(tasks = tasks, isLoading = false) // Set isLoading to false once data arrives
            }
            .stateIn(
                scope = viewModelScope,
                // Keep the upstream flow active for 5 seconds after the last collector disappears.
                // This is useful for configuration changes or short periods of inactivity.
                started = SharingStarted.WhileSubscribed(5_000L),
                // Initial state while data is loading or if the flow hasn't emitted yet.
                initialValue = TaskListUiState(isLoading = true)
            )

    /**
     * Adds a new task to the database.
     *
     * @param task The task to be added.
     */
    fun addTask(task: Task) {
        if (task.name.isBlank()) {
            // Optionally, update uiState with an error message or log
            // For example: _taskListUiState.update { it.copy(errorMessage = "Task name cannot be empty") }
            // For now, just return or log.
            println("TaskManagerViewModel: Task name cannot be blank.")
            return
        }

        viewModelScope.launch {
            try {

                tasksRepository.insertTask(task)
                // Optionally, clear any previous error messages on success
                // _taskListUiState.update { it.copy(errorMessage = null) }
            } catch (e: Exception) {
                // Handle exceptions, e.g., from database operations
                println("TaskManagerViewModel: Error adding task - ${e.message}")
                // Optionally, update uiState with an error message
                // _taskListUiState.update { it.copy(errorMessage = "Failed to add task: ${e.message}") }
            }
        }
    }

    /**
     * Call this function if you want to clear a displayed error message.
     * For example, after the user has acknowledged it.
     */
    fun clearErrorMessage() {
        // This requires taskListUiState to be a MutableStateFlow internally if you want to update it directly.
        // For simplicity with stateIn, error handling might be better done through a separate event Flow or State.
        // If taskListUiState was built on a MutableStateFlow:
        // _taskListUiState.update { it.copy(errorMessage = null) }
        println("TaskManagerViewModel: Error message clearing logic would go here if UI state was directly mutable.")
    }

    // You can add other methods here as needed, for example:
    // fun updateTask(task: Task) { viewModelScope.launch { tasksRepository.updateTask(task) } }
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
            tasksRepository.deleteTask(task)
        } catch (e:Exception) {
            println("TaskManagerViewModel: Error deleting task - ${e.message}")}
        }
    }
    // fun setTaskCompleted(task: Task, completed: Boolean) { /* ... */ }
}