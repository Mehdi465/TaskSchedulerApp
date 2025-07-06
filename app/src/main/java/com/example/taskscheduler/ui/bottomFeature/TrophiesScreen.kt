package com.example.taskscheduler.ui.bottomFeature

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.mainLogicUI.TaskManagerDestination
import com.example.taskscheduler.ui.navigation.NavigationDestination

object TrophiesDestination : NavigationDestination {
    override val route = "trophies"
    override val titleRes = R.string.trophies_screen

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrophiesScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToPomodoro: () -> Unit,
    navigateToTaskManager: () -> Unit,
){
    Scaffold(
        topBar = {
            TaskTopAppBar(
                title = stringResource(TrophiesDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        bottomBar = {
            BottomAppScheduleBar(
                onClickHome = navigateToHome,
                onClickPomodoro = navigateToPomodoro,
                onClickAddNewTask = navigateToTaskManager,
                onClickTrophies = {}
            )
        }
    )
     { innerPadding ->
        TrophiesContent(
            modifier = Modifier.padding(innerPadding),
        )

    }
}

@Composable
fun TrophiesContent(
    modifier: Modifier = Modifier
){
    Text(text = "Trophies")
}