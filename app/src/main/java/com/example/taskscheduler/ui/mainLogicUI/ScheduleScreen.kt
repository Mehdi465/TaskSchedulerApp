package com.example.taskscheduler.ui.mainLogicUI

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.data.Task.Companion.IconMap
import com.example.taskscheduler.ui.helperComposable.ValidateOrDeleteSession
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.theme.lighten
import com.example.taskscheduler.ui.theme.taskLighten
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModel
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModelFactory
import com.example.taskscheduler.utils.NotificationUtils
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.collections.get
import kotlin.math.roundToInt
import kotlin.text.format
import kotlin.text.get
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
    navigateToTracking: () -> Unit,
    modifier: Modifier = Modifier,
    scheduleViewModel: SharedSessionPomodoroViewModel = viewModel(
        factory = SharedSessionPomodoroViewModelFactory(
            (LocalContext.current.applicationContext as TaskApplication).activeSessionStore,
            (LocalContext.current.applicationContext as TaskApplication).taskTrackingRepository,
            (LocalContext.current.applicationContext as TaskApplication).sessionRepository,
            (LocalContext.current.applicationContext as TaskApplication).sessionTaskEntryRepository,
            (LocalContext.current.applicationContext as TaskApplication).tasksRepository,
            (LocalContext.current.applicationContext as TaskApplication).taskDeletedRepository))
) {
    val uiState by scheduleViewModel.uiState.collectAsState()

//    if (uiState.session != null) {
//        for (task in uiState.session!!.scheduledTasks){
//            Log.d("duration TASK","${task.task.name} : ${task.task.duration}")
//        }
//    }
    val liveCurrentTime = remember { mutableStateOf(getCurrentTimeSnappedToMinute()) }

    var showValidateOrDeleteDialog by remember { mutableStateOf(false) }

    // notification
    val context = LocalContext.current

    // --- LaunchedEffect to update liveCurrentTime every minute ---
    LaunchedEffect(Unit) {
        while (true) {
            liveCurrentTime.value = getCurrentTimeSnappedToMinute()
            // Calculate delay until the start of the next minute
            val now = Calendar.getInstance()
            val secondsUntilNextMinute = 60 - now.get(Calendar.SECOND)
            // delay to do it every minutes
            delay(secondsUntilNextMinute * 1000L)
        }
    }


    LaunchedEffect(uiState, liveCurrentTime.value) { // Re-run when session or time changes
        val session = uiState.session ?: return@LaunchedEffect
        val currentTime : Date = liveCurrentTime.value

        val currentTimeCalendar = Calendar.getInstance().apply {
            time = currentTime
        }

        session.scheduledTasks.forEach { scheduledTask ->

            val taskEndTimeCalendar = Calendar.getInstance().apply {
                time = scheduledTask.endTime
            }

            if (taskEndTimeCalendar.get(Calendar.HOUR_OF_DAY) == currentTimeCalendar.get(Calendar.HOUR_OF_DAY) &&
                taskEndTimeCalendar.get(Calendar.MINUTE) == currentTimeCalendar.get(Calendar.MINUTE)
                //!scheduledTasks.task.isBreak // Example: Don't notify for breaks, or handle differently
            ) {
                println("Task '${scheduledTask.task.name}' ended at ${scheduledTask.endTime.time}")
                NotificationUtils.sendTaskEndedNotification(context, scheduledTask.task.name)

            }
        }
    }


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
                onClickTrophies = navigateToTrophies,
                onClickTracking = navigateToTracking
            )},

    ) { innerPadding ->

        if (uiState.session == null) { //showDisplaySessionDialogAnswer ||
            //Text(stringResource(R.string.no_session_found))
            EmptyScheduleTimeline(modifier = Modifier.padding(innerPadding))
        } else {
            TimelineScreen(
                session = uiState.session!!,
                modifier = Modifier.padding(innerPadding),
                currentTime = liveCurrentTime.value,
                onReorderTasks = { from, to ->
                    scheduleViewModel.reorderScheduledTasks(from, to)
                }
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
                scheduleViewModel.onSessionValidated()

            }
        )
    }
}

@Composable
fun TimelineScreen(session: Session,
                   modifier: Modifier = Modifier,
                   currentTime : Date,
                   onReorderTasks: (from: Int, to: Int) -> Unit
) {
    val scheduledTasks = session.scheduledTasks

    Log.d("CURRNR_ID","${session.id}")

    var draggedItemIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }
    var currentDropTargetInfo by remember { mutableStateOf<Pair<Int, Float?>?>(null) }

    // to store the measured height of each item for drag calculations
    val itemHeights = remember { mutableStateMapOf<Any, Float>() } // Key by task.id
    val density = LocalDensity.current
    val hapticFeedback = LocalHapticFeedback.current

    if (!scheduledTasks.isEmpty()){
        Log.d("TimelineScreen", "scheduledTask not empty")
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            itemsIndexed(
                items = scheduledTasks,
                key = { _, scheduledTask -> scheduledTask.instanceId }
            ) { index, scheduledTask ->
                Log.d("TimelineScreen", "Rendering item ${scheduledTask.task.name}")

                val isBeingDragged = index == draggedItemIndex
                //val currentTaskHeightPx = itemHeights[scheduledTask.instanceId] ?: 0f

                // Visual displacement for items when another item is dragged over them
                val displacementOffset = when {
                    isBeingDragged -> 0f // Dragged item moves with finger
                    currentDropTargetInfo?.first == index && draggedItemIndex != null -> {
                        val draggedIdx = draggedItemIndex!!
                        val targetHeight = itemHeights[scheduledTasks[draggedIdx].instanceId] ?: 0f
                        if (draggedIdx < index) -targetHeight else targetHeight // Shift up or down
                    }
                    else -> 0f
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { size ->
                            // Convert px height to Dp then to Px with current density for consistency if needed,
                            // or just use raw pixel height if calculations are all in pixels.
                            itemHeights[scheduledTask.instanceId] = size.height.toFloat()
                        }
                        .zIndex(if (isBeingDragged) 1.0f else 0.0f) // Dragged item on top
                        .offset {
                            if (isBeingDragged) {
                                IntOffset(0, dragOffsetY.roundToInt())
                            } else {
                                // Apply displacement for items making space
                                IntOffset(0, displacementOffset.roundToInt())
                            }
                        }
                        .graphicsLayer { // Visuals for the dragged item
                            if (isBeingDragged) {
                                shadowElevation = 8.dp.toPx()
                                alpha = 0.95f
                                // scaleX = 1.03f
                                // scaleY = 1.03f
                            } else {
                                // Reset for non-dragged items, especially if they were previously dragged
                                shadowElevation = 0f
                                alpha = 1f
                                // scaleX = 1f
                                // scaleY = 1f
                            }
                        }
                        .pointerInput(scheduledTask.instanceId) { // Key pointerInput with task ID or index
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                    draggedItemIndex = index
                                    // dragOffsetY = 0f // Offset is relative to start of drag on item
                                },
                                onDragEnd = {
                                    draggedItemIndex?.let { fromIndex ->
                                        currentDropTargetInfo?.first?.let { toIndex ->
                                            if (fromIndex != toIndex) {
                                                // Check if the target index is valid for reordering
                                                // (e.g. not trying to drop on itself if no real movement)
                                                val actualToIndex =
                                                    if (fromIndex < toIndex) toIndex else toIndex
                                                if (fromIndex != actualToIndex) {
                                                    onReorderTasks(fromIndex, actualToIndex)
                                                }
                                            }
                                        }
                                    }
                                    draggedItemIndex = null
                                    dragOffsetY = 0f
                                    currentDropTargetInfo = null
                                },
                                onDragCancel = {
                                    draggedItemIndex = null
                                    dragOffsetY = 0f
                                    currentDropTargetInfo = null
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    dragOffsetY += dragAmount.y

                                    // --- Determine Drop Target ---
                                    val draggedItemCenterY =
                                        dragOffsetY + (itemHeights[scheduledTasks[index].instanceId]
                                            ?: 0f) / 2f
                                    var newTargetIndex = index // Default to current index

                                    // Check items above
                                    var accumulatedHeight = 0f
                                    for (i in index - 1 downTo 0) {
                                        val itemHeight =
                                            itemHeights[scheduledTasks[i].instanceId] ?: 0f
                                        if (draggedItemCenterY < accumulatedHeight + itemHeight) {
                                            newTargetIndex = i
                                        } else {
                                            break // No need to check further up
                                        }
                                        accumulatedHeight += itemHeight
                                    }

                                    // Check items below
                                    if (newTargetIndex == index) { // Only check below if not already targeted above
                                        accumulatedHeight =
                                            itemHeights[scheduledTasks[index].instanceId]
                                                ?: 0f // Start with current item's height
                                        for (i in index + 1 until scheduledTasks.size) {
                                            val itemHeight =
                                                itemHeights[scheduledTasks[i].instanceId] ?: 0f
                                            if (draggedItemCenterY > accumulatedHeight) {
                                                newTargetIndex = i
                                            } else {
                                                break //no need to check further down
                                            }
                                            accumulatedHeight += itemHeight
                                        }
                                    }
                                    currentDropTargetInfo = Pair(
                                        newTargetIndex,
                                        null
                                    ) // Second element could be drop fraction
                                }
                            )
                        }
                ) {
                    TaskItem(
                        scheduledTask = scheduledTask,
                        currentTime = currentTime
                        // TODO : add 'isBeingDragged' to TaskItem to change appearance
                    )
                }

                if (index < scheduledTasks.size - 1 && !isBeingDragged) { //\ Don't show spacer for the item being dragged
                    Spacer(modifier = Modifier.height(Dimens.spaceS))
                }
            }
        }
    } else {
        Log.d("TimelineScreen", "scheduledTask empty")
    }
}

/**
 * A scrollable timeline UI for when the schedule is empty.
 * It displays a timeline with round hours for the next 12 hours.
 * If the current time is 7:12, it will start the timeline from 8:00.
 *
 * @param modifier The modifier to be applied to the component.
 */
@Composable
fun EmptyScheduleTimeline(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    val timeFormat = remember { SimpleDateFormat("HH:00", Locale.getDefault()) }

    // --- Logic to get the next round hour ---
    val startCalendar = Calendar.getInstance()
    // Move to the next hour
    startCalendar.add(Calendar.HOUR_OF_DAY, 1)
    // Reset minutes, seconds, and milliseconds to zero
    startCalendar.set(Calendar.MINUTE, 0)
    startCalendar.set(Calendar.SECOND, 0)
    startCalendar.set(Calendar.MILLISECOND, 0)
    val startTime = startCalendar.time
    // --- End of logic ---


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Add a title or introductory text
        Text(
            text = "Your schedule is empty",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Generate the timeline for the next 12 hours from the rounded start time
        for (hourOffset in 0..12) {
            // Calculate the time for the current timeline marker
            val markerCalendar = Calendar.getInstance()
            markerCalendar.time = startTime
            markerCalendar.add(Calendar.HOUR_OF_DAY, hourOffset)
            val markerTime = markerCalendar.time

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp), // Represents one hour height
                verticalAlignment = Alignment.Top
            ) {
                // Timestamp on the left
                Text(
                    text = timeFormat.format(markerTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // Divider line
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp) // Align with the top of the text
                )
            }
        }
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


// Helper functions

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
