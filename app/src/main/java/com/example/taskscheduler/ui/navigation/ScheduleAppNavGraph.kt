package com.example.taskscheduler.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.ui.HomeDestination
import com.example.taskscheduler.ui.NewTaskScreen
import com.example.taskscheduler.ui.NewTaskScreenDestination
import com.example.taskscheduler.ui.ScheduleScreen
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
        composable(
            route = HomeDestination.route
        ) {
            ScheduleScreen(
                navigateToTaskManager = { navController.navigate(TaskManagerDestination.route) },
            )
        }

        // Task Manager Screen
        composable(
            route = TaskManagerDestination.route
        ) {
            TaskManagerScreen(
                navigateToTSessionManager = { selectedTaskIdsStr ->
                    navController.navigate(("${SessionDestination.route}/$selectedTaskIdsStr")) },
                navigateBack = { navController.popBackStack() },
                navigateToNewTaskScreen = {
                    Log.d("FAEKYHBF","GO to DNBWL")
                    navController.navigate(NewTaskScreenDestination.route)
                                          },
            )
        }

        // New Task Screen
        composable(
            route = NewTaskScreenDestination.route
        ){
            NewTaskScreen(
                onDismiss = { navController.popBackStack() }
            )

        }

        // Session Screen
        composable(
            route = SessionDestination.routeWithArgs,
            arguments = SessionDestination.arguments
            )
         { navBackStackEntry ->
            val selectedTaskIdsString =
                navBackStackEntry.arguments?.getString(SessionDestination.SELECTED_TASK_IDS_ARG)
            SessionScreen(
                selectedTaskIdsString = selectedTaskIdsString,
                navigateToSchedulePage = { navController.navigate(HomeDestination.route) },
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}