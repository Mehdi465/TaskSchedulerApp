package com.example.taskscheduler.ui.viewModel.tracking

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.foundation.layout.size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.OfflineSessionRepository
import com.example.taskscheduler.data.SessionRepository
import com.example.taskscheduler.data.SessionTaskEntryRepository
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.data.TaskTrackingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class TaskTrackingViewModel(
    private val taskRepository: TaskRepository,
    private val taskTrackingRepository: TaskTrackingRepository,
    private val sessionTaskEntryRepository: SessionTaskEntryRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = taskRepository.getAllTasksStream()
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTask(taskId: Int) = taskRepository.getTasksByIds(listOf(taskId))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // total session
    val totalSessionCount: StateFlow<Int> = sessionRepository.getPastSessions()
        .map { sessions -> sessions.size}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val totalTaskDoneCount: StateFlow<Int> = sessionTaskEntryRepository.getAllSessionTaskEntry()
        .map {sessionTaskEntryList -> sessionTaskEntryList.size}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val totalDuration: StateFlow<Long> = taskTrackingRepository.getAllTasksTrackingStream()
        .map{taskTrackingList -> taskTrackingList.sumOf{it.totalTimeMillisSpent}}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

}
