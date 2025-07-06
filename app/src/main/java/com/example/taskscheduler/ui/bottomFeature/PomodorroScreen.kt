package com.example.taskscheduler.ui.bottomFeature

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.ui.navigation.NavigationDestination
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
                   navigateToHome: () -> Unit,
                   navigateToTaskManager: () -> Unit,
                   navigateToTrophies: () -> Unit,
                   modifier: Modifier = Modifier,
                   scheduleViewModel: SharedSessionPomodoroViewModel = viewModel(
                       factory = SharedSessionPomodoroViewModelFactory(
                           (LocalContext.current.applicationContext as TaskApplication).activeSessionStore))
){
    val uiState by scheduleViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(PomodoroDestination.titleRes),
                canNavigateBack = true,
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
            session = uiState.session,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Composable
fun PomodoroContent(
    session: Session?,
    modifier : Modifier
){
    
}