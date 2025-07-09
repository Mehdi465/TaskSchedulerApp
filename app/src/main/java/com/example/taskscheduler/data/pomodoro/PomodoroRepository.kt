package com.example.taskscheduler.data.pomodoro

import android.content.Context
import android.content.Intent
import com.example.taskscheduler.data.PomodoroService
import com.example.taskscheduler.data.PomodoroState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


object PomodoroServiceConnector {
    val serviceStateFlow: MutableStateFlow<PomodoroState> = MutableStateFlow(PomodoroState())
}


class PomodoroRepository(private val context: Context) {

    val pomodoroState: StateFlow<PomodoroState> = PomodoroServiceConnector.serviceStateFlow.asStateFlow()

    fun startTimer() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
        }
        context.startService(intent)
    }

    fun pauseTimer() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeTimer() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stopTimer() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun skipPhase() {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_SKIP
        }
        context.startService(intent)
    }
}