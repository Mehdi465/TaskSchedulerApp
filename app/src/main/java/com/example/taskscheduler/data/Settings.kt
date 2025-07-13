package com.example.taskscheduler.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserPreferencesKeys {
    val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val POMODORO_WORK_DURATION = intPreferencesKey("pomodoro_work_duration_minutes")
    val POMODORO_BREAK_DURATION = intPreferencesKey("pomodoro_break_duration_minutes")
}

// DataStore instance
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)

// Repository/Class to interact with DataStore
class SettingsRepository(private val context: Context) {
    private val dataStore = context.userPreferencesDataStore

    val isDarkThemeEnabled: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.DARK_THEME_ENABLED] ?: false // Default value
        }

    suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.DARK_THEME_ENABLED] = isEnabled
        }
    }

    val pomodoroWorkDuration: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.POMODORO_WORK_DURATION] ?: 25 // Default to 25 minutes
        }

    suspend fun setPomodoroWorkDuration(durationMinutes: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.POMODORO_WORK_DURATION] = durationMinutes
        }
    }

    val pomodoroBreakDuration: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.POMODORO_BREAK_DURATION] ?: 25 // Default to 25 minutes
        }

    suspend fun setPomodoroBreakDuration(durationMinutes: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.POMODORO_BREAK_DURATION] = durationMinutes
        }
    }
}