package com.example.taskscheduler.ui.mainLogicUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.data.ScheduledTask
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.ui.navigation.NavigationDestination
import java.util.Date
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.data.Task.Companion.IconMap
import com.example.taskscheduler.ui.helperComposable.ValidateOrDeleteSession
import com.example.taskscheduler.ui.theme.lighten
import com.example.taskscheduler.ui.theme.taskLighten
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModel
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModelFactory
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

fun getCurrentTimeSnappedToMinute(): Date {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navigateToPomodoro: () -> Unit,
    navigateToTaskManager: () -> Unit,
    navigateToTrophies: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    scheduleViewModel: SharedSessionPomodoroViewModel = viewModel(
        factory = SharedSessionPomodoroViewModelFactory(
            (LocalContext.current.applicationContext as TaskApplication).activeSessionStore))
) {
    val uiState by scheduleViewModel.uiState.collectAsState()

    val liveCurrentTime = remember { mutableStateOf(getCurrentTimeSnappedToMinute()) }

    var showValidateOrDeleteDialog by remember { mutableStateOf(false) }

    // --- LaunchedEffect to update liveCurrentTime every minute ---
    LaunchedEffect(Unit) { // Keyed on Unit to run once and persist
        while (true) {
            liveCurrentTime.value = getCurrentTimeSnappedToMinute()
            // Calculate delay until the start of the next minute
            val now = Calendar.getInstance()
            val secondsUntilNextMinute = 60 - now.get(Calendar.SECOND)
            // delay to do it every minutes
            delay(secondsUntilNextMinute * 1000L)
        }
    }
    /*
    // --- End of Time Updater ---
    var hasAsked by remember { mutableStateOf(false) }
    var showDisplaySessionDialog by remember { mutableStateOf(false) }
    var showDisplaySessionDialogAnswer by remember { mutableStateOf(false) }


    if (uiState.session != null) {
        if (!showDisplaySessionDialogAnswer && !hasAsked){
        showDisplaySessionDialog = liveCurrentTime.value
            .after(uiState.session!!.endTime)
        }
    }
    */

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = "Schedule",
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = {
                        navigateToSettings()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }

                },
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showValidateOrDeleteDialog = true
                },
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Add"
                )
            }
        },

        bottomBar = {
            BottomAppScheduleBar(
                onClickHome = {},
                onClickPomodoro = navigateToPomodoro,
                onClickAddNewTask = navigateToTaskManager,
                onClickTrophies = navigateToTrophies
            )},

    ) { innerPadding ->

        if (uiState.session == null) { //showDisplaySessionDialogAnswer ||
            Text(stringResource(R.string.no_session_found))
        } else {
            TimelineScreen(
                session = uiState.session!!,
                modifier = Modifier.padding(innerPadding),
                currentTime = liveCurrentTime.value
            )
        }
    }

    if (showValidateOrDeleteDialog){
        ValidateOrDeleteSession(
            onDismiss = {showValidateOrDeleteDialog = false},
            onDelete = {
                showValidateOrDeleteDialog = false
                scheduleViewModel.clearActiveSession()
            },
            onValidate = {
                showValidateOrDeleteDialog = false
                // TODO increase stats
            }
        )
    }

    /*
    if (showDisplaySessionDialog) {
        RemoveSessionDialog(
            onDismiss = {
                showDisplaySessionDialog = false
                hasAsked = true
                        },
            onConfirm = {
                showDisplaySessionDialogAnswer = true
                showDisplaySessionDialog = false
                hasAsked = true
            }
        )
    }
    */
}

@Composable
fun TimelineScreen(session: Session,
                   modifier: Modifier = Modifier,
                   currentTime : Date
) {
    var scheduledTasks by remember { mutableStateOf(session.scheduledTasks)}
    if (!scheduledTasks.isEmpty()){
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            itemsIndexed(scheduledTasks) { index, scheduledTask ->
                TaskItem(
                    scheduledTask = scheduledTask,
                    currentTime = currentTime
                )
                if (index < scheduledTasks.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                    )
                }
            }
        }
    }
    else {
        Text(stringResource(R.string.no_tasks_scheduled_etc)) // TODO : no inspiration
    }
}

@Composable
fun TaskItem(
    scheduledTask: ScheduledTask,
    currentTime: Date,
    onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .height(getCardHeightInDp(scheduledTask))
            .background(
                if (scheduledTask.isCurrentTask(currentTime)) {
                    Color.DarkGray
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "${getHoursString(scheduledTask.startTime)} : ${getMinutesString(scheduledTask.startTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "${getHoursString(scheduledTask.endTime)} : ${getMinutesString(scheduledTask.endTime)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        var cardColor by remember { mutableStateOf(scheduledTask.task.color.taskLighten()) }

        TaskCard(
            backgroundColor = cardColor,
            scheduledTask,
            modifier = Modifier.clickable(onClick = onClick),
            onColorChange = { newColor ->
                cardColor = newColor
            }
        )
    }
}

fun getHoursString(date: Date): String {
    if (date.hours > 9) {
        return date.hours.toString()
    } else {
        return "0" + date.hours.toString()
    }
}

fun getMinutesString(date: Date): String {
    if (date.minutes > 9) {
        return date.minutes.toString()
    } else {
            return "0" + date.minutes.toString()
    }
}


@Composable
fun TaskCard(
            backgroundColor: Color,
            scheduledTask: ScheduledTask,
            modifier: Modifier,
            onColorChange : (Color) -> Unit
    )
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(getCardHeightInDp(scheduledTask)),

        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
        ) {

            var isChecked by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .background(
                        if (!isChecked) scheduledTask.task.color
                        else scheduledTask.task.color.lighten(0.7f)
                    )
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                IconTask(scheduledTask.task.icon)
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                // Task Name text
                StrikethroughText(
                    scheduledTask.name,
                    isStrikethrough = isChecked,
                    style = MaterialTheme.typography.titleMedium
                )

                StrikethroughText(
                    text = "Duration: ${getDurationAsString(scheduledTask.startTime,
                        scheduledTask.endTime)}",
                    isStrikethrough = isChecked,
                    style = MaterialTheme.typography.bodySmall
                )

                // From - To text
                StrikethroughText(
                    text = "From: ${scheduledTask.startTime.hours}h ${scheduledTask.startTime.minutes}m" +
                            " - To: ${scheduledTask.endTime.hours}h ${scheduledTask.endTime.minutes}m",
                    isStrikethrough = isChecked,
                    style = MaterialTheme.typography.bodySmall
                )
            }



            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    isChecked = !isChecked
                    onColorChange(if (isChecked) Color.LightGray else scheduledTask.task.color.taskLighten())
                                  },
                colors  = CheckboxDefaults.colors(
                    checkedColor = scheduledTask.task.color,
                    checkmarkColor = Color.DarkGray,
                    uncheckedColor = Color.DarkGray
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun StrikethroughText(
    text: String,
    isStrikethrough: Boolean,
    style: TextStyle
) {
    Text(
        text = text,
        style = if (isStrikethrough) {
            style.copy(textDecoration = TextDecoration.LineThrough)
        } else {
            style.copy(textDecoration = TextDecoration.None)
        },
        color = Color.DarkGray
    )
}


@Composable
fun IconTask(iconName:String?) {
    val iconResId = IconMap.getIconResId(iconName)

    Image(
        painter = painterResource(id = iconResId),
        contentDescription = "Task icon"
    )
}


fun getRelativeDuration(duration: Duration): Int {
    val result = duration.toLong(DurationUnit.MINUTES)
    return (result).toInt()
}

fun getCardHeightInDp(scheduledTask: ScheduledTask): Dp {
    val minThreshold = 15.minutes
    val maxThreshold = 3.hours

    if (scheduledTask.task.duration < minThreshold){
         return 80.dp
    }
    else if (scheduledTask.task.duration  > maxThreshold) {
        return 80.dp
    }
    else {
        val finalDp = (0.727*(getRelativeDuration(scheduledTask.task.duration))+69.1).dp
        return finalDp
    }
}

fun getDurationAsString(startTime: Date, endTime: Date): String {
    if (endTime.before(startTime) || endTime == startTime) {
        return "0h 0m" // No duration or negative duration
    }

    // Get the difference in milliseconds
    val durationInMillis = endTime.time - startTime.time

    // Convert milliseconds to hours and minutes
    val hours = TimeUnit.MILLISECONDS.toHours(durationInMillis)
    val remainingMillisAfterHours = durationInMillis - TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillisAfterHours)
    if (hours == 0L){
        return "${minutes}m"
    }
    return "${hours}h ${minutes}m"
}
