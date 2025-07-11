package com.example.taskscheduler.ui.bottomFeature

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.PomodoroPhase
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.pomodoro.PomodoroViewModel
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroUiState
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModel
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModelFactory

// To be modifiable in parameters
/*
val workTime
val smallbreakTime
val longBreakTime
*/

object PomodoroDestination : NavigationDestination {
    override val route = "pomodoro"
    override val titleRes = R.string.pomodoro_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
                   navigateBack: () -> Unit,
                   navigateToHome: () -> Unit,
                   navigateToTaskManager: () -> Unit,
                   navigateToTrophies: () -> Unit,
                   modifier: Modifier = Modifier,
                   scheduleViewModel: SharedSessionPomodoroViewModel = viewModel(
                       factory = SharedSessionPomodoroViewModelFactory(
                           (LocalContext.current.applicationContext as TaskApplication).activeSessionStore))
){
    val uiStateShared by scheduleViewModel.uiState.collectAsState()


    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(PomodoroDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        bottomBar = {
            BottomAppScheduleBar(
                onClickHome = navigateToHome,
                onClickPomodoro = {}, // do nothing
                onClickAddNewTask = navigateToTaskManager,
                onClickTrophies = navigateToTrophies
            )
        }
    ) { innerPadding ->
        PomodoroContent(
            sharedUiState = uiStateShared,
            session = uiStateShared.session,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Composable
fun PomodoroContent(
    sharedUiState : SharedSessionPomodoroUiState,
    session: Session?,
    modifier : Modifier
){
    val viewModel: PomodoroViewModel = viewModel()
    viewModel.startTimer()

    PomodoroTimerApp(viewModel)
}

@Composable
fun PomodoroTimerApp(viewModel: PomodoroViewModel) {
    // Observe the PomodoroState from the ViewModel's StateFlow
    // collectAsStateWithLifecycle is preferred as it's lifecycle-aware
    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()
    Log.d("PomodoroTimerApp", "PomodoroState: $pomodoroState.timeLeftMillis")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (pomodoroState.phase) {
                PomodoroPhase.WORK -> "Work Time"
                PomodoroPhase.BREAK -> "Short Break"
                PomodoroPhase.LONG_BREAK -> "Long Break"
                PomodoroPhase.PAUSED -> "Paused"
                PomodoroPhase.STOPPED -> "Stopped"
            },
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = viewModel.formatTime(pomodoroState.timeLeftMillis),
            fontSize = 72.sp,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.startTimer() },
                enabled = pomodoroState.phase == PomodoroPhase.STOPPED
            ) {
                Text("Start")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (pomodoroState.phase == PomodoroPhase.PAUSED) {
                        viewModel.resumeTimer()
                    } else {
                        viewModel.pauseTimer()
                    }
                },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text(if (pomodoroState.phase == PomodoroPhase.PAUSED) "Resume" else "Pause")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.skipPhase() },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text("Skip")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.stopTimer() },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text("Stop")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cycles: ${pomodoroState.totalCycles} (Current: ${pomodoroState.currentCycle})",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PomodoroTimerTheme {
        // For preview, we can't directly use the ViewModel.
        // Provide a dummy state or a simplified mock for visual preview.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Work Time (Preview)",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "25:00",
                fontSize = 72.sp,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /* Do nothing for preview */ }) { Text("Start") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* Do nothing for preview */ }) { Text("Pause") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* Do nothing for preview */ }) { Text("Skip") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { /* Do nothing for preview */ }) { Text("Stop") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cycles: 0 (Current: 0)",
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun PomodoroTimerTheme(content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}