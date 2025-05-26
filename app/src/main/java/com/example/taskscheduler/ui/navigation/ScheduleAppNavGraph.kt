package com.example.taskscheduler.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.taskscheduler.TaskScreens
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.HomeDestination
import com.example.taskscheduler.ui.ScheduleSreen
import com.example.taskscheduler.ui.SessionDestination
import com.example.taskscheduler.ui.SessionScreen
import com.example.taskscheduler.ui.TaskManagerDestination
import com.example.taskscheduler.ui.TaskManagerScreen

@Composable
fun TaskAppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
){
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ){
        composable(route = HomeDestination.route) {
            ScheduleSreen(
                navigateToTaskManager = { navController.navigate(TaskManagerDestination.route) },
                session = Session.sessionWithDefaults
            )
        }
        composable(route = TaskManagerDestination.route) {
            TaskManagerScreen(
                navigateToTSessioManager = { navController.navigate(SessionDestination.route) },
                navigateBack = { navController.popBackStack() },
                tasks = Task.DEFAULT_TASKS
            )
        }
        composable(route= SessionDestination.route) {
            SessionScreen(
                selectedTasks = Task.DEFAULT_TASKS,
                navigateToSchedulePage = { navController.navigate(HomeDestination.route) },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}