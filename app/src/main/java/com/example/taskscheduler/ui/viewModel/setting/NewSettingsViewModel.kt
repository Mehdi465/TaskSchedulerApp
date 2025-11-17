package com.example.taskscheduler.ui.viewModel.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.data.BackgroundMode
import com.example.taskscheduler.data.NewSettings
import com.example.taskscheduler.data.NewSettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NewSettingsViewModel (application: Application) : AndroidViewModel(application) {

    private val dataStore = NewSettingsDataStore(application)

    val settings = dataStore.newSettings.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        NewSettings()
    )

    fun saveSettings(settings: NewSettings) {
        viewModelScope.launch {
            dataStore.saveNewSettings(settings)
        }
    }

    fun saveBackgroundMode(newBackgroundMode: BackgroundMode){
        viewModelScope.launch {
            dataStore.saveBackgroundMode(newBackgroundMode)
        }
    }

    fun saveWorkTime(newWorkTime: Int){
        viewModelScope.launch {
            dataStore.saveWorkTime(newWorkTime)
        }
    }

    fun saveBreakTime(newBreakTime: Int){
        viewModelScope.launch {
            dataStore.saveBreakTime(newBreakTime)
        }
    }

    fun saveThemeColor(newThemeColor: Long){
        viewModelScope.launch {
            dataStore.saveThemeColor(newThemeColor)
        }
    }

    fun saveLanguage(newLanguage : String){
        viewModelScope.launch {
            dataStore.saveLanguage(newLanguage)
        }
    }
}