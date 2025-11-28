package com.example.taskscheduler.ui.mainLogicUI

import android.app.Dialog
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.helperComposable.EraseDatabaseDialog
import com.example.taskscheduler.ui.helperComposable.SelectTimeDialog
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.viewModel.setting.SettingsViewModel
import com.example.taskscheduler.ui.viewModel.setting.SettingsViewModelFactory
import kotlin.time.Duration

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            (LocalContext.current.applicationContext as TaskApplication).settingsRepository,
            (LocalContext.current.applicationContext as TaskApplication).tasksRepository
        )
    )
){
    val isDarkThemeEnabled by settingsViewModel.isDarkThemeEnabled.collectAsState()
    val pomodoroWorkDuration by settingsViewModel.pomodoroWorkDuration.collectAsState()
    val pomodoroBreakDuration by settingsViewModel.pomodoroBreakDuration.collectAsState()

    Scaffold(
        modifier = Modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(R.string.settings_screen),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
    ) {innerPadding ->
        SettingContent(
            modifier = Modifier.padding(innerPadding),
            settingsViewModel = settingsViewModel,
            isDarkThemeEnabled = isDarkThemeEnabled,
            onThemeChange = {
                settingsViewModel.setDarkThemeEnabled(it)
            },
            pomodoroWorkDuration = pomodoroWorkDuration,
            onPomodoroWorkDurationChange = { hour, minutes ->
                settingsViewModel.setPomodoroWorkDuration(hour*60+minutes)
            },
            pomodoroBreakDuration = pomodoroBreakDuration,
            onPomodoroBreakDurationChange = { hour, minutes ->
                settingsViewModel.setPomodoroBreakDuration(hour*60+minutes)
            },
            onBreakTimeChange = {
                settingsViewModel.setPomodoroBreakDuration(it)
            },
            onWorkTimeChange = {
                settingsViewModel.setPomodoroWorkDuration(it)
            }
        )
    }
}

@Composable
fun SettingContent(
    modifier: Modifier,
    settingsViewModel : SettingsViewModel,
    isDarkThemeEnabled : Boolean,
    onThemeChange: (Boolean) -> Unit,
    pomodoroWorkDuration : Int,
    onPomodoroWorkDurationChange : (Int,Int) -> Unit,
    pomodoroBreakDuration : Int,
    onPomodoroBreakDurationChange : (Int,Int) -> Unit,
    onBreakTimeChange : (Int) -> Unit,
    onWorkTimeChange : (Int) -> Unit
) {

    // dialog declaration
    var displaySelectTimeBreakDialog by remember { mutableStateOf(false) }
    var displaySelectTimeWorkDialog by remember { mutableStateOf(false) }
    var displayEraseDatabase by remember { mutableStateOf(false) }

    val isSystemInDarkTheme = isSystemInDarkTheme()

    Column(
        modifier = modifier // Apply the modifier from Scaffold here
            .fillMaxSize() // Allow Column to take up all available space from Scaffold
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp) // Overall horizontal padding for content
            //.background(color = Color.White)
    ) {
        // --- Pomodoro Section ---
        Text(
            text = stringResource(R.string.pomodoro_settings),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // break time
        SettingsTile(
            text = stringResource(R.string.pomodoro_settings)+": $pomodoroBreakDuration" + stringResource(R.string.minutes), // Display current value
            controlContent = {
                Button(onClick = {
                    displaySelectTimeBreakDialog = true
                }
                ) {
                    Text(text = stringResource(R.string.change))
                }
            }
        )
        // work time
        Spacer(modifier = Modifier.height(Dimens.spaceS)) // space between Pomodoro tiles
        SettingsTile(
            text = stringResource(R.string.work_time)+": $pomodoroWorkDuration" + stringResource(R.string.minutes),
            controlContent = {
                Button(onClick = {
                    displaySelectTimeWorkDialog = true
                }) {
                    Text(text = stringResource(R.string.change))
                }
            }
        )

        // add more settings as needed
        Spacer(modifier = Modifier.height(16.dp)) // Bottom padding

        // --- Colors Section ---
        Text(
            text = stringResource(R.string.color),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        SettingsTile(
            text = stringResource(R.string.theme_color),
            controlContent = {
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.change))
                    }
            }
        )

        //  ------ Language Section --------
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        SettingsTile(
            text = stringResource(R.string.current_language),
        ) { }

        // ------- Delete all datas Part -------
        Text(
            text = stringResource(R.string.erase_all_data),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        SettingsTile(
            text = stringResource(R.string.erase_all_data),
            controlContent = {
                Button(onClick = {displayEraseDatabase=true}) {
                    Text(text = stringResource(R.string.erase))
                }
            }
        )

        // ------- Dialog Part -------

        // Erase database
        if (displayEraseDatabase){
            EraseDatabaseDialog(
                onDismiss = {
                    displayEraseDatabase = false
                },
                onConfirm = {
                    settingsViewModel.onResetDatabaseClicked()
                    displayEraseDatabase = false
                }
            )
        }

        // Work Time Dialog
        if (displaySelectTimeWorkDialog) {
            SelectTimeDialog(
                onDismiss = {
                    displaySelectTimeWorkDialog = false
                },
                themeColor = Color.Blue,
                onTimeSelected = { a, b ->
                    onPomodoroWorkDurationChange(a, b)
                },
                onTimeStore = {
                    onWorkTimeChange
                    displaySelectTimeWorkDialog = false
                }
            )
        }

        // Break Time Dialog
        if (displaySelectTimeBreakDialog) {
            SelectTimeDialog(
                onDismiss = {
                    displaySelectTimeBreakDialog = false
                },
                themeColor = Color.Blue,
                onTimeSelected = {a,b ->
                    onPomodoroBreakDurationChange(a,b)
                },
                onTimeStore = {
                    onBreakTimeChange
                    displaySelectTimeWorkDialog = false
                }
            )
        }
    }
}

/**
 * A reusable Composable for a settings item row.
 * Displays text on the left and a custom control (Switch, Button, etc.) on the right.
 */
@Composable
fun SettingsTile(
    text: String,
    modifier: Modifier = Modifier,
    controlContent: @Composable () -> Unit // Lambda for the control on the right
) {

    var isModifyBreakTime = false

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(72.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize() // Fill the Card
                .padding(horizontal = 16.dp), // Padding inside the Row
            verticalAlignment = Alignment.CenterVertically, // Vertically center content in Row
            horizontalArrangement = Arrangement.SpaceBetween // Push Text and Control apart
        ) {
            // Text on the left, takes available space
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f, fill = false) // Allow it to take space but not fill if short
            )

            // Control on the right (Switch, Button, etc.)
            Box { // Box to ensure the control itself is also centered if it has padding/margin
                controlContent()
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    SettingScreen(navigateBack = {})
}