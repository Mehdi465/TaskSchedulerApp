package com.example.taskscheduler.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


enum class BackgroundMode{
    SYSTEM_MODE,
    DARK_MODE,
    LIGHT_MODE,
}

// default values
data class NewSettings(
    val background: String = BackgroundMode.SYSTEM_MODE.name, // store it as a string
    val breakTime: Int = 5,
    val workTime: Int = 30,
    val color: Long = 0xff000000,
    val language: String = "en"
)

class NewSettingsDataStore(private val context: Context){
    companion object {
        private val Context.datastore: DataStore<Preferences> by preferencesDataStore("SettingsDataStore")
        private val BACKGROUND_MODE = stringPreferencesKey("backgroundMode")
        private val BREAKTIME = intPreferencesKey("breakTime")
        private val WORKTIME = intPreferencesKey("workTime")
        private val THEME_COLOR = longPreferencesKey("themeColor")
        private val LANGUAGE = stringPreferencesKey("language")
    }

    val newSettings : Flow<NewSettings> = context.datastore.data.map { preferences ->
        NewSettings(
            preferences[BACKGROUND_MODE] ?: BackgroundMode.SYSTEM_MODE.name,
            preferences[BREAKTIME] ?: 5,
            preferences[WORKTIME] ?: 30,
            preferences[THEME_COLOR] ?: 0xFF000000,
            preferences[LANGUAGE] ?: "en"
        )
    }

    suspend fun saveNewSettings(settings: NewSettings){
        context.datastore.edit { preferences ->
            preferences[BACKGROUND_MODE] = settings.background
            preferences[BREAKTIME] = settings.breakTime
            preferences[WORKTIME] = settings.workTime
            preferences[THEME_COLOR] = settings.color
            preferences[LANGUAGE] = settings.language
        }
    }

    suspend fun saveBackgroundMode(newValue: BackgroundMode){
        context.datastore.edit { preferences ->
            preferences[BACKGROUND_MODE] = newValue.name
        }
    }

    suspend fun saveBreakTime(newValue: Int){
        context.datastore.edit { preferences ->
            preferences[BREAKTIME] = newValue
        }
    }

    suspend fun saveWorkTime(newValue: Int){
        context.datastore.edit { preferences ->
            preferences[WORKTIME] = newValue
        }
    }

    suspend fun saveThemeColor(newValue: Long){
        context.datastore.edit { preferences ->
            preferences[THEME_COLOR] = newValue
        }
    }

    suspend fun saveLanguage(newValue: String){
        context.datastore.edit { preferences ->
            preferences[LANGUAGE] = newValue
        }
    }
}