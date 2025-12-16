package com.example.taskscheduler.data.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.PomodoroPhase
import com.example.taskscheduler.data.PomodoroService
import com.example.taskscheduler.data.PomodoroState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn


val DEFAULT_WORK_DURATION = 25L
val DEFAULT_BREAK_DURATION = 5L

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PomodoroRepository = PomodoroRepository(application)

    //expose the pomodoroState from the repository as a StateFlow for the UI
    val pomodoroState: StateFlow<PomodoroState> = repository.pomodoroState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep the flow active as long as there are subscribers (UI)
            initialValue = PomodoroState(
            )
        )

    fun startTimer() {
        repository.startTimer()
    }

    fun pauseTimer() {
        repository.pauseTimer()
    }

    fun resumeTimer() {
        repository.resumeTimer()
    }

    fun stopTimer() {
        repository.stopTimer()
    }

    fun skipPhase() {
        repository.skipPhase()
    }

    fun getInitialState(): PomodoroState {
        // Read the initial duration directly from the repository.
        // This is a synchronous operation.
        val workDuration = DEFAULT_WORK_DURATION
        val breakDuration = DEFAULT_BREAK_DURATION

        return PomodoroState(
            phase = PomodoroPhase.STOPPED,
            workDuration = workDuration,
            brakeDuration = breakDuration,
            timeLeftMillis = workDuration,
        )
    }

    // Helper function to format time for display in the UI
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}