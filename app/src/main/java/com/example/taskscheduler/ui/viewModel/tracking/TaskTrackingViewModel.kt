package com.example.taskscheduler.ui.viewModel.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.Repository.SessionRepository
import com.example.taskscheduler.data.Repository.SessionTaskEntryRepository
import com.example.taskscheduler.data.Repository.TaskDeletedRepository
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.Repository.TaskRepository
import com.example.taskscheduler.data.TaskTracking
import com.example.taskscheduler.data.Repository.TaskTrackingRepository
import com.example.taskscheduler.data.TaskDeleted
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Date


class TaskTrackingViewModel(
    private val taskRepository: TaskRepository,
    private val taskTrackingRepository: TaskTrackingRepository,
    private val sessionTaskEntryRepository: SessionTaskEntryRepository,
    private val sessionRepository: SessionRepository,
    private val taskDeletedRepository: TaskDeletedRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = taskRepository.getAllTasksStream()
        .stateIn(viewModelScope,
            SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasksTracking : StateFlow<List<TaskTracking>> = taskTrackingRepository.getAllTasksTrackingStream()
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

    val totalDuration: StateFlow<Long> = sessionTaskEntryRepository.getAllSessionTaskEntry()
        .map{taskTrackingList -> taskTrackingList.sumOf{(it.endTime.time - it.startTime.time)}}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val lastWeekSessions: StateFlow<List<Session>> = sessionRepository.getLastWeekSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<Session>()
        )

    val firstSession: StateFlow<Session?> = sessionRepository.getFirstSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Session(
                id = -1, // Use an invalid ID to indicate it's a placeholder
                startTime = Date(0L),
                endTime = Date(0L),
                scheduledTasks = emptyList()
            )
        )

    val lastSession: StateFlow<Session?> = sessionRepository.getLastSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Session(
                id = -1, // Use an invalid ID to indicate it's a placeholder
                startTime = Date(0L),
                endTime = Date(0L),
                scheduledTasks = emptyList()
            )
        )


    val mostDoneTaskTracked: StateFlow<TaskTracking?> = taskTrackingRepository.getMostDoneTask()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskTracking(
                taskId = -1,
            )
        )

    val mostDoneTask: StateFlow<Task?> = mostDoneTaskTracked
        //this block runs every time 'mostDoneTaskTracked' emits a new value.
        .flatMapLatest { taskTracking ->
            if (taskTracking != null && taskTracking.taskId != -1) {
                taskRepository.getTaskStream(taskTracking.taskId)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // The initial value for the final result is also null.
        )

    val deletedTasks: StateFlow<List<TaskDeleted>> = taskDeletedRepository.getAllTaskDeleted()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList<TaskDeleted>()
        )


}
