package com.example.taskscheduler.ui.viewModel.taskmanager

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

/**
 * UI state for the Task List screen
 */
data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val checkedTasks: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
)

/**
 * ViewModel to retrieve and manage task data for the TaskManagerPage.
 *
 * @param tasksRepository The repository for accessing task data.
 */
class TaskManagerViewModel(
    private val tasksRepository: TaskRepository // Injected via TaskViewModelFactory
) : ViewModel() {

    private val _checkedTaskIds = MutableStateFlow<Set<Int>>(emptySet())
    private val errorMessages = MutableStateFlow<String>(value = "")

    private val _taskBackgroundColors = MutableStateFlow<MutableList<Color>>(mutableListOf())
    val taskBackgroundColors: StateFlow<List<Color>> = _taskBackgroundColors.asStateFlow()


    /**
     * Holds the UI state for the task list.
     * This StateFlow is observed by the UI to display tasks and loading states.
     */
    val taskListUiState: StateFlow<TaskListUiState> =
        combine(
            tasksRepository.getAllTasksStream(), // Flow<List<Task>>
            _checkedTaskIds,                     // Flow<Set<Int>>
            errorMessages // not yet used
        ) { tasks: List<Task>, checkedIds: Set<Int>, errorMsg: String? ->
            // This mapping function is called whenever tasks, checkedIds, or errorMsg emit a new value
            TaskListUiState(
                tasks = tasks,
                checkedTasks = checkedIds,
                isLoading = false, // Set to false because tasks are now available from the stream.
                // The initialValue of stateIn handles the initial loading state.
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            // The initialValue's isLoading will be true.
            // Once getAllTasksStream emits its first list (even if empty),
            // the combine block will run, and isLoading will become false.
            initialValue = TaskListUiState(isLoading = true, checkedTasks = _checkedTaskIds.value)
        )

    /**
     * Toggles the selection state of a task from the Switch Button.
     * @param taskId The ID of the task to toggle.
     */
    fun toggleTaskSelectionSwitchButton(taskId: Int) {
        _checkedTaskIds.update { currentCheckedIds ->
            val newCheckedIds = currentCheckedIds.toMutableSet()
            if (!newCheckedIds.contains(taskId)) {
                newCheckedIds.add(taskId)
            }
            newCheckedIds
        }
    }


    /**
     * Toggles the selection state of a task from the CheckedBox.
     * @param taskId The ID of the task to toggle.
     */
    fun toggleTaskSelection(taskId: Int) {
        _checkedTaskIds.update { currentCheckedIds ->
            val newCheckedIds = currentCheckedIds.toMutableSet()
            if (newCheckedIds.contains(taskId)) {
                newCheckedIds.remove(taskId)
                newCheckedIds
            } else {
                newCheckedIds.add(taskId)
                newCheckedIds
            }
        }
    }

    /**
     * Gets the list of currently selected Task objects.
     * @return A list of selected Tasks.
     */
    fun getSelectedTasks(): List<Task> {
        // We need to access the current list of tasks and checked IDs.
        // taskListUiState.value contains the latest combined state.
        val currentUiState = taskListUiState.value // Get the current emitted state
        return currentUiState.tasks.filter { task ->
            task.id in currentUiState.checkedTasks
        }
    }

    /**
     * Clears all current task selections.
     */
    fun clearSelections() {
        _checkedTaskIds.value = emptySet()
    }


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