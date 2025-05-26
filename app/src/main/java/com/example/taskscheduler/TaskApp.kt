@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskscheduler

import androidx.annotation.StringRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.navigation.TaskAppNavHost


enum class TaskScreens(@StringRes val title: Int){
    Schedule(title = R.string.schedule_screen),
    TaskManager(title = R.string.task_manager_screen),
    SessionManager(title = R.string.session_screen)
    // TODO add other screens
}

@Composable
fun TaskApp(navController: NavHostController = rememberNavController()){
    TaskAppNavHost(navController = navController)
}

@Composable
fun TaskTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}
