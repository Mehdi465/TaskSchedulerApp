package com.example.taskscheduler.ui.viewModel.tracking

import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.data.TaskTrackingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn


class TaskTrackingViewModel(
    private val taskRepository: TaskRepository,
    private val taskTrackingRepository: TaskTrackingRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = taskRepository.getAllTasksStream()
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTask(taskId: Int) = taskRepository.getTasksByIds(listOf(taskId))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

}
