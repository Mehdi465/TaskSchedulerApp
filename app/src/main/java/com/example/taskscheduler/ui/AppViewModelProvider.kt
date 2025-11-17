package com.example.taskscheduler.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.ui.viewModel.taskmanager.TaskManagerViewModel
import com.example.taskscheduler.ui.viewModel.tracking.TaskTrackingViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskManagerViewModel(taskApplication().container.tasksRepository)
        }

        initializer {
            TaskTrackingViewModel(
                sessionRepository = taskApplication().container.sessionRepository,
                taskRepository = taskApplication().container.tasksRepository,
                taskTrackingRepository = taskApplication().container.taskTrackingRepository,
                sessionTaskEntryRepository = taskApplication().container.sessionTaskEntryRepository
            )
        }
    }
}

fun CreationExtras.taskApplication(): TaskApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication)