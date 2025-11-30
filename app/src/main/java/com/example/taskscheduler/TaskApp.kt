@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.taskscheduler

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.navigation.TaskAppNavHost

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
    navigateUp: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
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
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF222222),//MaterialTheme.colorScheme.primary,
//            titleContentColor = MaterialTheme.colorScheme.onPrimary,
//            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
//            actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}

@Composable
fun BottomAppScheduleBar(
    onClickHome : () -> Unit = {},
    onClickPomodoro : () -> Unit = {},
    onClickAddNewTask : () -> Unit = {},
    onClickTracking : () -> Unit = {},
    onClickTrophies : () -> Unit = {}
) {
    BottomAppBar(
        containerColor = Color(0xFF222222)
    ) {
        // Schedule
        IconButton(
            onClick = onClickHome,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Filled.Home,
                contentDescription = stringResource(R.string.back_button)
            )
        }

        // Pomodoro
        IconButton(
            onClick = onClickPomodoro,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Filled.Lock,
                contentDescription = stringResource(R.string.back_button)
            )
        }

        // Task Manager
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ){
            FloatingActionButton(
                onClick = onClickAddNewTask
            ) {
                Icon(
                    imageVector = Filled.Add,
                    contentDescription = "Add"
                )
            }
        }

        // tracking
        IconButton(
            onClick = onClickTracking,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Filled.Check,
                contentDescription = stringResource(R.string.back_button)
            )
        }

        // Trophies
        IconButton(
            onClick = onClickTrophies,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Filled.Settings,
                contentDescription = stringResource(R.string.back_button)
            )
        }
    }
}
