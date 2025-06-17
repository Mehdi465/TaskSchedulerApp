package com.example.taskscheduler.ui

import android.R.attr.textSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.room.util.TableInfo
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.viewModel.SessionViewModel
import com.example.taskscheduler.ui.viewModel.SessionViewModelFactory
import java.time.LocalTime


object SessionDestination : NavigationDestination {
    override val route = "session_screen"
    override val titleRes = R.string.session_screen
    const val SELECTED_TASK_IDS_ARG = "selectedTaskIds"
    val routeWithArgs = "$route/{$SELECTED_TASK_IDS_ARG}"
    val arguments = listOf(
        navArgument(SELECTED_TASK_IDS_ARG) {
            type = NavType.StringType
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    modifier : Modifier = Modifier,
    selectedTaskIdsString : String?,
    navigateToSchedulePage: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateBack: () -> Unit,
) {

    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
    val context = LocalContext.current
    val taskRepository = (context.applicationContext as TaskApplication).container.tasksRepository
    val factory = SessionViewModelFactory(taskRepository, savedStateRegistryOwner)
    val sessionViewModel: SessionViewModel = viewModel(factory = factory)
    val uiState by sessionViewModel.uiState.collectAsState()

    val selectedTasks = uiState.loadedSelectedTasks


    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = "Session Manager",
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToSchedulePage,
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Create Session"
                )
            }
        }
    ) { innerPadding ->
        SessionCreationPage(
            selectedTasks = emptyList(),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SessionCreationPage(
    modifier: Modifier = Modifier,
    selectedTasks : List<Task>,
){
    var startTime by remember { mutableStateOf(LocalTime.now()) }
    var endTime by remember { mutableStateOf(LocalTime.now().plusHours(1)) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        TimerPicker(initialTime = LocalTime.now()) { time ->
            startTime = time
        }
        Text("Selected Start: $startTime")

        Spacer(modifier = Modifier.height(32.dp))

        TimerPicker(initialTime = LocalTime.now().plusHours(1)) { time ->
            endTime = time
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text("Selected End: $endTime")
    }
}

@Composable
fun TimerPicker(
    modifier: Modifier = Modifier,
    initialTime: LocalTime = LocalTime.now(),
    onTimeSelected: (LocalTime) -> Unit
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val initialHourIndex = hours.indexOf(initialTime.hour)
    val initialMinuteIndex = minutes.indexOf(initialTime.minute)

    val hourState = rememberLazyListState(initialHourIndex)
    val minuteState = rememberLazyListState(initialMinuteIndex)

    val selectedHour = remember { derivedStateOf { hours.getOrElse(hourState.firstVisibleItemIndex) { 0 } } }
    val selectedMinute = remember { derivedStateOf { minutes.getOrElse(minuteState.firstVisibleItemIndex) { 0 } } }

    // Call callback with current LocalTime
    LaunchedEffect(selectedHour.value, selectedMinute.value) {
        val selectedTime = LocalTime.of(selectedHour.value, selectedMinute.value)
        onTimeSelected(selectedTime)
    }

    Row(
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeWheel(
            values = hours.map { it.toString().padStart(2, '0') },
            state = hourState,
        )
        TimeWheel(
            values = minutes.map { it.toString().padStart(2, '0') },
            state = minuteState,
        )
    }
}

@Composable
fun TimeWheel(
    values: List<String>,
    state: LazyListState,
) {
    Box(modifier = Modifier.width(80.dp)) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center),
            contentPadding = PaddingValues(vertical = 70.dp),
            verticalArrangement = Arrangement.Center
        ) {
            items(values.size) { index ->
                val selected = state.firstVisibleItemIndex == index
                Text(
                    text = values[index],
                    fontSize = if (selected) 32.sp else 24.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Overlay to indicate selection area
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(48.dp)
                .border(1.dp, Color.Gray, RectangleShape)
        )
    }
}



@Preview
@Composable
fun PagePreview(){
    SessionCreationPage(modifier = Modifier, selectedTasks = Task.DEFAULT_TASKS)
}