package com.example.taskscheduler.ui.mainLogicUI

import android.text.Layout
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
    modifier: Modifier,
    // Example state and callbacks
    onThemeChange: (Boolean) -> Unit = {},
    onBreakTimeChangeClick: () -> Unit = {},
    onWorkTimeChangeClick: () -> Unit = {},
    currentThemeIsDark: Boolean = false,
    currentBreakTime: String = "5 min",
    currentWorkTime: String = "25 min"
) {
    Column(
        modifier = modifier // Apply the modifier from Scaffold here
            .fillMaxSize() // Allow Column to take up all available space from Scaffold
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp) // Overall horizontal padding for content
    ) {
        // Optional: Add a top spacer if needed, but innerPadding from Scaffold handles top bar
        // Spacer(modifier = Modifier.height(16.dp))

        // --- Display Options Section ---
        Text(
            text = "Display Options",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        SettingsTile(
            text = "Dark Theme", // Changed text for clarity
            controlContent = {
                Switch(
                    checked = currentThemeIsDark, // Use actual state
                    onCheckedChange = onThemeChange // Use callback
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp)) // Space between sections

        // --- Pomodoro Section ---
        Text(
            text = "Pomodoro Settings",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        SettingsTile(
            text = "Break Time: $currentBreakTime", // Display current value
            controlContent = {
                Button(onClick = onBreakTimeChangeClick) {
                    Text(text = "Change")
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp)) // Space between Pomodoro tiles
        SettingsTile(
            text = "Work Time: $currentWorkTime", // Display current value
            controlContent = {
                Button(onClick = onWorkTimeChangeClick) {
                    Text(text = "Change")
                }
            }
        )

        // Add more settings as needed
        Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
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
            .padding(vertical = 4.dp) // Small padding around the card
            .height(72.dp), // Adjust height as needed, or let content define it
        // colors = CardDefaults.cardColors( // Default colors usually adapt to theme
        // containerColor = Color.DarkGray // Consider removing hardcoded colors
        // )
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