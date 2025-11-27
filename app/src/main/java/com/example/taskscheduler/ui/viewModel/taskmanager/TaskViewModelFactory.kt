package com.example.taskscheduler.ui.viewModel.taskmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.data.Repository.TaskDeletedRepository
import com.example.taskscheduler.data.Repository.TaskRepository
import com.example.taskscheduler.data.Repository.TaskTrackingRepository
import kotlin.jvm.java


class TaskViewModelFactory(
    private val tasksRepository: TaskRepository,
    private val taskDeletedRepository: TaskDeletedRepository,
    private val taskTrackingRepository: TaskTrackingRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskManagerViewModel::class.java)) {
            return TaskManagerViewModel(
                tasksRepository,
                taskDeletedRepository,
                taskTrackingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}