package com.example.taskscheduler

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.data.pomodoro.PomodoroViewModel
import com.example.taskscheduler.ui.theme.TaskSchedulerTheme
import com.example.taskscheduler.ui.viewModel.setting.SettingsViewModel
import com.example.taskscheduler.ui.viewModel.setting.SettingsViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PomodoroViewModel

    // Request permission launcher for POST_NOTIFICATIONS (Android 13+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted.")
        } else {
            Log.w("MainActivity", "Notification permission denied. Foreground service notifications might not be shown.")
            // Consider showing a rationale to the user here
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission if on Android 13 (API 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        viewModel = ViewModelProvider(this).get(PomodoroViewModel::class.java)

        enableEdgeToEdge()
        setContent {

            val application = applicationContext as TaskApplication
            val settingsRepository = application.settingsRepository

            // Instantiate SettingsViewModel (or use Hilt for DI)
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(settingsRepository, application.tasksRepository)
            )

            // Collect the dark theme state
            val isDarkTheme by settingsViewModel.isDarkThemeEnabled.collectAsState()

            // Apply the theme based on the collected state

            TaskSchedulerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White,

                ) {
                    TaskApp()
                }
            }
        }
    }
}
