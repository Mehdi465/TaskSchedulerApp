package com.example.taskscheduler.ui.viewModel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.SettingsRepository // Your repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    val isDarkThemeEnabled: StateFlow<Boolean> = settingsRepository.isDarkThemeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false // Or a loading state initially
        )

    val pomodoroWorkDuration: StateFlow<Int> = settingsRepository.pomodoroWorkDuration
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 25 // Default, will be updated by DataStore
        )

    val pomodoroBreakDuration: StateFlow<Int> = settingsRepository.pomodoroBreakDuration
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 5 // Default, will be updated by DataStore
        )


    // --- Functions to update settings ---

    fun setDarkThemeEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkThemeEnabled(isEnabled)
        }
    }

    fun setPomodoroWorkDuration(durationMinutes: Int) {
        viewModelScope.launch {
            // TODO set validation, can be gt work (for lazy ass)
            settingsRepository.setPomodoroWorkDuration(durationMinutes)
        }
    }

    fun setPomodoroBreakDuration(durationMinutes: Int) {
        viewModelScope.launch {
            settingsRepository.setPomodoroBreakDuration(durationMinutes)
        }
    }

    // add other functions
}

// Factory for creating SettingsViewModel with dependencies (SettingsRepository)
class SettingsViewModelFactory(private val settingsRepository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}