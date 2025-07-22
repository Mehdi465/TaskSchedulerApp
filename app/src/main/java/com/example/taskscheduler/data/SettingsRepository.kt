package com.example.taskscheduler.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Define your Preference Keys (can be in this file or a separate Keys.kt file)
object UserPreferencesKeysTest {
    val DARK_THEME_ENABLED = booleanPreferencesKey("dark_theme_enabled")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled") // Add this if you need it
    val POMODORO_WORK_DURATION = intPreferencesKey("pomodoro_work_duration_minutes")
    val POMODORO_BREAK_DURATION = intPreferencesKey("pomodoro_break_duration_minutes")
    // Add other keys as needed
}

// Define the DataStore instance at the top level (outside any class)
// This makes it a singleton accessible via the Context extension.
private const val USER_SETTINGS_NAME = "user_settings" // Name for your DataStore file

val Context.context: DataStore<Preferences> by preferencesDataStore(
    name = USER_SETTINGS_NAME
)

// The Repository class to interact with DataStore
class SettingsRepositorywudyg(private val context: Context) {

    // Shortcut to access the DataStore instance
    private val dataStore = context.userPreferencesDataStore

    // --- Dark Theme Setting ---
    val isDarkThemeEnabled: Flow<Boolean> = dataStore.data
        .catch { exception -> // Handle potential IOExceptions when reading data
            if (exception is IOException) {
                emit(emptyPreferences()) // Emit empty preferences on error
            } else {
                throw exception // Rethrow other exceptions
            }
        }
        .map { preferences ->
            // Use a default value if the key is not found
            preferences[UserPreferencesKeys.DARK_THEME_ENABLED] ?: false
        }

    suspend fun setDarkThemeEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.DARK_THEME_ENABLED] = isEnabled
        }
    }

    // --- Pomodoro Work Duration Setting ---
    val pomodoroWorkDuration: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[UserPreferencesKeys.POMODORO_WORK_DURATION] ?: 25 // Default to 25 minutes
        }

    suspend fun setPomodoroWorkDuration(durationMinutes: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.POMODORO_WORK_DURATION] = durationMinutes
        }
    }

    // --- Pomodoro Break Duration Setting ---
    val pomodoroBreakDuration: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[UserPreferencesKeys.POMODORO_BREAK_DURATION] ?: 5 // Default to 5 minutes
        }

    suspend fun setPomodoroBreakDuration(durationMinutes: Int) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.POMODORO_BREAK_DURATION] = durationMinutes
        }
    }


    // --- Notifications Enabled Setting (Example for future use) ---
    val areNotificationsEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] ?: true // Default to true
        }

    suspend fun setNotificationsEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.NOTIFICATIONS_ENABLED] = isEnabled
        }
    }

    // Add more settings flows and update functions here as needed
}
