package com.example.taskscheduler.ui.viewModel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.SettingsRepository // Your repository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Data class to hold all settings values for the UI
data class SettingsUiState(
    val isDarkThemeEnabled: Boolean = false,
    val pomodoroWorkDuration: Int = 25,
    val pomodoroBreakDuration: Int = 5,
    // Add other settings as needed
    val isLoading: Boolean = false // Could be useful if initial load is slow
)

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    // Expose a StateFlow for the entire UI state related to settings
    // This combines multiple flows from the repository into one state object for the UI
    // For simplicity, I'm showing direct mapping. For more complex scenarios,
    // you might combine multiple flows:
    // val uiState: StateFlow<SettingsUiState> = combine(
    //     settingsRepository.isDarkThemeEnabled,
    //     settingsRepository.pomodoroWorkDuration,
    //     settingsRepository.pomodoroBreakDuration
    // ) { darkTheme, workDuration, breakDuration ->
    //     SettingsUiState(
    //         isDarkThemeEnabled = darkTheme,
    //         pomodoroWorkDuration = workDuration,
    //         pomodoroBreakDuration = breakDuration
    //     )
    // }.stateIn(
    //     scope = viewModelScope,
    //     started = SharingStarted.WhileSubscribed(5000),
    //     initialValue = SettingsUiState(isLoading = true) // Start with a loading state
    // )

    // Simpler individual StateFlows if you prefer to collect them separately in the UI
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
            // Add validation if needed (e.g., durationMinutes > 0)
            settingsRepository.setPomodoroWorkDuration(durationMinutes)
        }
    }

    fun setPomodoroBreakDuration(durationMinutes: Int) {
        viewModelScope.launch {
            // Add validation if needed
            settingsRepository.setPomodoroBreakDuration(durationMinutes)
        }
    }

    // Add functions for other settings (e.g., notifications)
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