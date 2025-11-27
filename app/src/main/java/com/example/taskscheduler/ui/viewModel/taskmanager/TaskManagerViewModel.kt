package com.example.taskscheduler.ui.viewModel.taskmanager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Repository.TaskDeletedRepository
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.Repository.TaskRepository
import com.example.taskscheduler.data.Repository.TaskTrackingRepository
import com.example.taskscheduler.data.TaskDeleted
import com.example.taskscheduler.data.TaskTracking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlin.text.toLong

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
    private val tasksRepository: TaskRepository, // Injected via TaskViewModelFactory
    private val taskDeletedRepository: TaskDeletedRepository,
    private val taskTrackingRepository: TaskTrackingRepository
) : ViewModel() {

    private val _checkedTaskIds = MutableStateFlow<Set<Int>>(emptySet())
    val checkedTaskIds: StateFlow<Set<Int>> = _checkedTaskIds.asStateFlow()

    private val errorMessages = MutableStateFlow<String>(value = "")


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
            TaskListUiState(
                tasks = tasks,
                checkedTasks = checkedIds,
                isLoading = false,

            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
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
        val currentUiState = taskListUiState.value
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
                    val trackingInfo: TaskTracking? = taskTrackingRepository
                        .getTrackingForTaskOnce(task.id)

                    val archivedTask = TaskDeleted(
                        taskId = task.id,
                        name = task.name,
                        priority = task.priority.name,
                        icon = task.icon.toString(),
                        color = task.color.toString(),
                        timesCompleted = trackingInfo?.timesCompleted ?: 0,
                        totalTimeMillisSpent = trackingInfo?.totalTimeMillisSpent ?: 0L
                    )

                    taskDeletedRepository.upsertTask(archivedTask)
                    tasksRepository.deleteTask(task)
        } catch (e:Exception) {
            Log.d("TaskManagerViewModel","Error deleting task - ${e.message}")
            }
        }
    }
}