package com.example.taskscheduler.ui

import android.R.attr.textSize
import android.app.Application
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskRepository
import com.example.taskscheduler.ui.viewModel.SessionViewModel
import com.example.taskscheduler.ui.viewModel.SessionViewModelFactory
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days
import com.example.taskscheduler.ui.TimePickerV2
import kotlin.text.get
import kotlin.text.set


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
    application: Application = LocalContext.current.applicationContext as Application,
    tasksRepository: TaskRepository = (LocalContext.current.applicationContext as TaskApplication).container.tasksRepository
) {

    val sessionViewModel : SessionViewModel = viewModel(
        factory = SessionViewModelFactory(
            application = application,
            taskRepository = tasksRepository,
            owner = LocalSavedStateRegistryOwner.current, // For SavedStateHandle if factory/VM uses it for OTHER things
            defaultArgs = (LocalViewModelStoreOwner.current as? NavBackStackEntry)?.arguments, // Pass bundle for SSH
            initialTaskIdsString = selectedTaskIdsString
        )
    )

    val uiStateTasks by sessionViewModel.uiState.collectAsState()

    val selectedTasks = uiStateTasks.loadedSelectedTasks

    var sessionStartTime by remember { mutableStateOf(Date())}
    var sessionEndTime by remember { mutableStateOf(Date())}

    val isSaving by sessionViewModel.isSavingSession.collectAsState()
    val navigateToHomeTrigger by sessionViewModel.sessionSaveCompleteAndNavigate.collectAsState()
    val errorMessage by sessionViewModel.saveErrorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // handle navigation
    LaunchedEffect(navigateToHomeTrigger) {
        if (navigateToHomeTrigger) {
            navigateToSchedulePage() // Call the navigation lambda passed from NavHost
            sessionViewModel.onNavigationToScheduleHomeComplete() // Reset the trigger in VM
        }
    }

    // handle error messages
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            sessionViewModel.clearSaveErrorMessage() // Clear message after showing
        }
    }

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
                onClick = {if (!isSaving) {
                    sessionViewModel.onConfirmAndSaveSession(
                        sessionStartTime = sessionStartTime,
                        sessionEndTime = sessionEndTime,
                        tasks = selectedTasks
                    )}},
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                if (isSaving) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Session"
                    )
                }
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
    calendar.time = this
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

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

        fun displayTime(currentDateTime: Date): String {
            val parts = currentDateTime.toString().split(" ")

            val first = parts.getOrNull(0)
            val third = parts.getOrNull(2)
            val fourth = parts.getOrNull(3)
                ?.split(":")
                ?.dropLast(1)
                ?.joinToString(":")

            return listOfNotNull(first, third, fourth).joinToString(" ")
        }

        //Text("Selected Start: ${displayTime(sessionStartTime)}")
        TimePickerV2(
            initialStartTime = initialStartHour*60+initialStartMinute,
            initialEndTime = initialEndHour*60+initialEndMinute,
            onTimeChange = { startTime, endTime ->

                val (newStartDate, newEndDate) = convertMinutesToStartEndDates(
                    currentStartDate = sessionStartTime,
                    newStartTimeMinutes = startTime,
                    newEndTimeMinutes = endTime
                )

                onStartTimeChange(newStartDate)
                onEndTimeChange(newEndDate)
            }
        )
    }
}

fun convertMinutesToStartEndDates(
    currentStartDate: Date, // Pass the current sessionStartTime to base the new dates on its day
    newStartTimeMinutes: Int,
    newEndTimeMinutes: Int
): Pair<Date, Date> {
    if (newStartTimeMinutes !in 0 until (24 * 60) || newEndTimeMinutes !in 0 until (24 * 60)) {
        // Log an error or handle it, maybe return current dates to avoid crash
        Log.e("TimeConversion", "Invalid input minutes: start=$newStartTimeMinutes, end=$newEndTimeMinutes")
        // Fallback to avoid crashing, though ideally the TimePickerV2 should prevent this.
        // Or you might want to signal an error to the user.
        val tempCal = Calendar.getInstance()
        tempCal.time = currentStartDate
        val currentStartHour = tempCal.get(Calendar.HOUR_OF_DAY)
        val currentStartMinute = tempCal.get(Calendar.MINUTE)

        tempCal.time = currentStartDate // Assuming currentStartDate is the reference for current "end time" before change
        val currentEndHour = tempCal.get(Calendar.HOUR_OF_DAY) // This logic for fallback end date might need refinement
        val currentEndMinute = tempCal.get(Calendar.MINUTE)
        tempCal.add(Calendar.HOUR_OF_DAY, 1) // Default to 1 hour after current start if invalid

        return Pair(
            currentStartDate.updateTime(currentStartHour, currentStartMinute),
            currentStartDate.updateTime(currentEndHour, currentEndMinute).let {
                if (newEndTimeMinutes < newStartTimeMinutes && it.before(currentStartDate.updateTime(currentStartHour,currentStartMinute))) {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    cal.add(Calendar.DAY_OF_MONTH, 1)
                    cal.time
                } else {
                    it
                }
            }
        )
    }

    val calendar = Calendar.getInstance()

    // --- Create Start Date ---
    // Use the day from currentStartDate as the base for the new startTime
    calendar.time = currentStartDate // Set calendar to the day of the existing sessionStartTime
    calendar.set(Calendar.HOUR_OF_DAY, newStartTimeMinutes / 60)
    calendar.set(Calendar.MINUTE, newStartTimeMinutes % 60)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val resultingStartDate: Date = calendar.time

    // --- Create End Date ---
    // Use the day from currentStartDate as the base initially
    calendar.time = currentStartDate // Reset to the day of the existing sessionStartTime
    calendar.set(Calendar.HOUR_OF_DAY, newEndTimeMinutes / 60)
    calendar.set(Calendar.MINUTE, newEndTimeMinutes % 60)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)

    if (newEndTimeMinutes < newStartTimeMinutes) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    // If startTime and endTime are the same, and it's not 00:00 (which could be a 0-minute session)
    // assume it's a 24-hour session ending on the next day.
    // Adjust this specific condition (&& newStartTimeMinutes != 0) if 00:00 to 00:00 should always be 24h.
    else if (newEndTimeMinutes == newStartTimeMinutes && newStartTimeMinutes != 0) {
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }


    val resultingEndDate: Date = calendar.time

    return Pair(resultingStartDate, resultingEndDate)
}



