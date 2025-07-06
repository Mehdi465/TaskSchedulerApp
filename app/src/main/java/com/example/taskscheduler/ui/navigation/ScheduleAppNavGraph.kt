package com.example.taskscheduler.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskscheduler.ui.bottomFeature.TrophiesDestination
import com.example.taskscheduler.ui.bottomFeature.TrophiesScreen
import com.example.taskscheduler.ui.mainLogicUI.HomeDestination
import com.example.taskscheduler.ui.mainLogicUI.NewTaskScreen
import com.example.taskscheduler.ui.mainLogicUI.NewTaskScreenDestination
import com.example.taskscheduler.ui.mainLogicUI.ScheduleScreen
import com.example.taskscheduler.ui.mainLogicUI.SessionDestination
import com.example.taskscheduler.ui.mainLogicUI.SessionScreen
import com.example.taskscheduler.ui.mainLogicUI.TaskManagerDestination
import com.example.taskscheduler.ui.mainLogicUI.TaskManagerScreen

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
                navigateToTrophies = { navController.navigate(TrophiesDestination.route) },
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
                    navController.navigate(NewTaskScreenDestination.route)
                                          },
                navigateToModifyTaskScreen = { taskId ->
                    navController.navigate(("${NewTaskScreenDestination.route}?" +
                            "${NewTaskScreenDestination.TASK_ID_ARG}=$taskId"))
                }
            )
        }

        // New Task Screen
        composable(
            route = "${NewTaskScreenDestination.route}?" +
                    "${NewTaskScreenDestination.TASK_ID_ARG}={${NewTaskScreenDestination.TASK_ID_ARG}}",
            arguments = listOf(
                navArgument(NewTaskScreenDestination.TASK_ID_ARG) {
                    type = NavType.IntType
                    defaultValue = -1
                    }
            )
        ){navBackStackEntry ->
            val taskId = navBackStackEntry.arguments?.getInt(NewTaskScreenDestination.TASK_ID_ARG)
            NewTaskScreen(
                onDismiss = { navController.popBackStack() },
                taskIdToModify = if (taskId != null && taskId != -1) taskId else null
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




         /*
          POMODORO, TASK TRACKING AND TROPHIES
          */
        composable(
            route = TrophiesDestination.route
        ) {
            TrophiesScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}