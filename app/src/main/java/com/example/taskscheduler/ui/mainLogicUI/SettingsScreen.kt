package com.example.taskscheduler.ui.mainLogicUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.UserPreferencesKeys
import com.example.taskscheduler.ui.navigation.NavigationDestination

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navigateBack: () -> Unit,
){
    Scaffold(
        modifier = Modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(R.string.settings_screen),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        }
    ) {innerPadding ->
        SettingContent(
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun SettingContent(
    modifier: Modifier
){
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())

    ) {
        val reusableItemModifier = Modifier.weight(1f)

        // Dark/Light Mode Section
        Row(
            modifier = reusableItemModifier
            ) {
                Text(text = "Switch Theme Mode")

                Switch(checked = true, onCheckedChange = {})
            }

        // Pomodoro Section
        Text(text = "Pomodoro Settings")
        Row(
            modifier = reusableItemModifier
        ) {
            // Break  Time
            PomodoroSettingsTile(
                text = "Break Time",
                mode = "break"
            )
            // Work Time
            PomodoroSettingsTile(
                text = "Work Time",
                mode = "work"
            )
        }
    }
}

@Composable
fun PomodoroSettingsTile(
    text : String,
    objectPreferenceKey : UserPreferencesKeys = UserPreferencesKeys,
    mode : String // allows to choose what element to change
) {
    Row() {
        Text(text = text)

        when (mode) {
            "work" -> {

            }

            "break" -> {

            }
        }
    }
}