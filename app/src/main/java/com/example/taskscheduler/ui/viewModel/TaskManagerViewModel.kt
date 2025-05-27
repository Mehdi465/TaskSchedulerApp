package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TaskManagerViewModel (taskRepository: TaskRepository) : ViewModel() {

    val taskManagerUiState: StateFlow<TaskManagerUiState> = taskRepository.getAllTasksStream().map{
        TaskManagerUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = TaskManagerUiState()
    )

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TaskManagerUiState(val tasksList: List<Task> = listOf())