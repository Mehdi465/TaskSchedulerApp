package com.example.taskscheduler.ui

import android.R.attr.textSize
import android.util.Log
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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


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
    selectedTaskIdsString : String?,
    modifier : Modifier = Modifier,
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
    var sessionStartTime by remember { mutableStateOf(Date())}
    var sessionEndTime by remember { mutableStateOf(Date())}


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
            sessionStartTime = sessionStartTime,
            sessionEndTime = sessionEndTime,
            onStartTimeChange = { updatedStartTime : Date ->
                sessionStartTime = updatedStartTime
            },
            onEndTimeChange = { updatedEndTime : Date ->
                sessionEndTime = updatedEndTime
            },
            selectedTasks = emptyList(),
            modifier = Modifier.padding(innerPadding)
        )
    }
}

fun Date.updateTime(hour: Int, minute: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this // Set calendar to current date object
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

// SimpleDateFormat for displaying the time part of the Date
private val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

@Composable
fun SessionCreationPage(
    sessionStartTime: Date,
    sessionEndTime: Date,
    onStartTimeChange : (Date) -> Unit,
    onEndTimeChange :(Date) -> Unit,
    modifier: Modifier = Modifier,
    selectedTasks : List<Task>,
){

    val startCalendar = Calendar.getInstance().apply { time = sessionStartTime }
    val initialStartHour = startCalendar.get(Calendar.HOUR_OF_DAY)
    val initialStartMinute = startCalendar.get(Calendar.MINUTE)

    val endCalendar = Calendar.getInstance().apply { time = sessionEndTime }
    val initialEndHour = endCalendar.get(Calendar.HOUR_OF_DAY)
    val initialEndMinute = endCalendar.get(Calendar.MINUTE)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        TimerPicker(
            initialHour = initialStartHour,
            initialMinute = initialStartMinute,
            onTimeSelected = { hour, minute ->
                val newStartTime = sessionStartTime.updateTime(hour, minute)
                if (newStartTime.before(sessionEndTime) || newStartTime == sessionEndTime) {
                    onStartTimeChange(newStartTime)
                } else {
                    onStartTimeChange(newStartTime)
                    val suggestedEndTime = Calendar.getInstance().apply {
                        time = newStartTime
                        add(Calendar.HOUR_OF_DAY, 1)
                    }.time
                    onEndTimeChange(suggestedEndTime)
                }
            }
        )
        Text("Selected Start: $sessionStartTime")

        Spacer(modifier = Modifier.height(32.dp))

        TimerPicker(
            initialHour = initialEndHour,
            initialMinute = initialEndMinute,
            onTimeSelected = { hour, minute ->
                val newEndTime = sessionEndTime.updateTime(hour, minute)
                // Ensure end time is after start time
                if (newEndTime.after(sessionStartTime)) {
                    onEndTimeChange(newEndTime)
                } else {
                    // Handle invalid selection (end time is before or same as start time)
                    // For example, show a toast, or don't update, or auto-adjust
                    // For now, let's just log it or you could prevent the update.
                    // A better UX would be to provide feedback.
                    // Or, as done above for start time, auto-adjust.
                    // For simplicity, we can choose not to update if invalid or adjust start time.
                    // To prevent end time being before start:
                    // onEndTimeChange(sessionStartTime.updateTime( (hour+1)%24, minute)) // or some logic
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("Selected End: $sessionEndTime")
    }
}

@Composable
fun TimerPicker(
    modifier: Modifier = Modifier,
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val validInitialHour = initialHour.coerceIn(0, 23)
    val validInitialMinute = initialMinute.coerceIn(0, 59)

    val hourState = rememberLazyListState(validInitialHour)
    val minuteState = rememberLazyListState(validInitialMinute)

    val selectedHour = remember { derivedStateOf { hours.getOrElse(hourState.firstVisibleItemIndex) { 0 } } }
    val selectedMinute = remember { derivedStateOf { minutes.getOrElse(minuteState.firstVisibleItemIndex) { 0 } } }

    // Call callback with current LocalTime
    LaunchedEffect(selectedHour.value, selectedMinute.value) {
        Log.d("TimerPicker", "Time changed: Hour=${selectedHour.value}, Minute=${selectedMinute.value}")
        onTimeSelected(selectedHour.value, selectedMinute.value)
    }

    LaunchedEffect(validInitialHour) {
        hourState.scrollToItem(validInitialHour)
    }
    LaunchedEffect(validInitialMinute) {
        minuteState.scrollToItem(validInitialMinute)
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
                val isActuallySelected = (state.firstVisibleItemIndex +
                        (state.layoutInfo.visibleItemsInfo.size / 2)) == index
                Text(
                    text = values[index],
                    fontSize = if (isActuallySelected) 32.sp else 24.sp,
                    fontWeight = if (isActuallySelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActuallySelected) MaterialTheme.colorScheme.primary else Color.Gray,
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

