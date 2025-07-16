package com.example.taskscheduler.ui.mainLogicUI

import android.text.Layout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
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
        },
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

        Spacer(modifier = Modifier.padding(50.dp))

        // Dark/Light Mode Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .height(80.dp)
        ) {
            Row(
                modifier = Modifier
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = "Switch Theme Mode"
                )
                
                Box(
                    modifier = Modifier
                        .padding(start = 100.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Switch(
                        modifier = Modifier,
                        checked = true,
                        onCheckedChange = {})
                }            
            }
        }

        // Pomodoro Section
        Text(text = "Pomodoro Settings")
        Column(
            modifier = Modifier
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )

    ) {
        Row() {
            Text(text = text)

            when (mode) {
                "work" -> {
                    Button(onClick = {}) {
                        Text(text = "Change")
                    }
                }

                "break" -> {
                    Button(onClick = {}) {
                        Text(text = "Change")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    SettingScreen(navigateBack = {})
}