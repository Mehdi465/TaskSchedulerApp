package com.example.taskscheduler.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import kotlinx.coroutines.launch

class NewTaskViewModel(
    private val tasksRepository: TaskRepository // Injected via TaskViewModelFactory
) : ViewModel() {
    /**
     * Adds a new task to the database.
     *
     * @param task The task to be added.
     */
    fun addTask(task: Task) {
        if (task.name.isBlank()) {
            println("TaskManagerViewModel: Task name cannot be blank.")
            return
        }
        viewModelScope.launch {
            try {
                tasksRepository.insertTask(task)
            } catch (e: Exception) {
                println("TaskManagerViewModel: Error adding task - ${e.message}")
            }
        }
    }
}