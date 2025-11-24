package com.example.taskscheduler.ui.viewModel.newtask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.Repository.TaskRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewTaskViewModel(
    private val tasksRepository: TaskRepository // Injected via TaskViewModelFactory
) : ViewModel() {

    private var loadTasksJob: Job? = null

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

    fun updateTask(task: Task) {
        if (task.name.isBlank()) {
            println("TaskManagerViewModel: Task name cannot be blank.")
            return
        }
        viewModelScope.launch {
            try {
                tasksRepository.updateTask(task)
            } catch (e: Exception) {
                println("TaskManagerViewModel: Error adding task - ${e.message}")
            }
        }
    }

    fun getTaskByIdAsFlow(taskId: Int): Flow<Task?> {
        if (taskId <= 0) {
            return emptyFlow() // Or flowOf(null) if you want an immediate null emission
        }
        return tasksRepository.getTasksByIds(listOf(taskId))
            .map { taskList ->
                taskList.firstOrNull() // Get the first task from the list, or null
            }
            .catch { e ->
                Log.e("TaskManagerViewModel", "Error getting task flow for ID $taskId", e)
                emit(null) // Emit null in case of an error in the flow
            }
    }
}
