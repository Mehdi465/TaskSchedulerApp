package com.example.taskscheduler.data.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.PomodoroState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PomodoroRepository = PomodoroRepository(application)

    // Expose the PomodoroState from the repository as a StateFlow for the UI
    val pomodoroState: StateFlow<PomodoroState> = repository.pomodoroState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep the flow active as long as there are subscribers (UI)
            initialValue = PomodoroState() // Provide an initial value
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

    // Helper function to format time for display in the UI
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}