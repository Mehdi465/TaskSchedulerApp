package com.example.taskscheduler.ui

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.ui.viewModel.TaskManagerViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            TaskManagerViewModel(taskApplication().container.tasksRepository)
        }
    }
}


fun CreationExtras.taskApplication(): TaskApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as TaskApplication)