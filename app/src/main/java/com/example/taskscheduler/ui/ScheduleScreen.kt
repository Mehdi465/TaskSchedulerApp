package com.example.taskscheduler.ui

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.taskscheduler.ui.viewModel.ScheduleViewModel
import com.example.taskscheduler.ui.viewModel.ScheduleViewModelFactory
import androidx.compose.ui.platform.LocalContext

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navigateToTaskManager: () -> Unit,
    modifier: Modifier = Modifier,
    scheduleViewModel: ScheduleViewModel = viewModel(
        factory = ScheduleViewModelFactory(
            (LocalContext.current.applicationContext as TaskApplication).activeSessionStore))
){
    val uiState by scheduleViewModel.uiState.collectAsState()
    uiState.session

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = "Schedule",
                canNavigateBack = false,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTaskManager,
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add"
                )
            }
        },
    ) { innerPadding ->
        if (uiState.session == null){
            Text("No session selected")
        }
        else {
            TimelineScreen(
                session = uiState.session!!,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
fun TimelineScreen(session: Session,
                   modifier: Modifier = Modifier,
) {
    var tasks by remember { mutableStateOf(session.scheduledTasks)}//sortedBy { it.task.durationStamp }) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        itemsIndexed(tasks) { index, task ->
            TaskItem(task)
            if (index < tasks.size - 1) {
                Spacer(modifier = Modifier
                    .height(10.dp)
                    )//TODO : Implement dynamic spacer
            }
        }
    }
}

@Composable
fun TaskItem(scheduledTask: ScheduledTask, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .height(getCardHeightInDp(scheduledTask))
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

        TaskCard(
            scheduledTask,
            modifier = Modifier.clickable(onClick = onClick)
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
fun TaskCard(scheduledTask: ScheduledTask,modifier: Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(80.dp),//getCardHeightInDp(scheduledTask)), // TODO: Calculate the height based on the duration

        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        )
    ) {
        Row(
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .background(color = scheduledTask.task.color)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.book),//scheduledTask.task.icon!!),
                    contentDescription = "Task logo",
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {

                Text(
                    text = scheduledTask.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = "Duration: ${scheduledTask.task.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "From: ${scheduledTask.startTime.hours}h ${scheduledTask.startTime.minutes}m - To: ${scheduledTask.endTime.hours}h ${scheduledTask.endTime.minutes}m", // Use a locale-aware date format
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Checkbox(
                checked = false,
                onCheckedChange = {}, //TODO : make the card change color when clicked,
                colors = CheckboxDefaults.colors(Color.Red),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

// TODO : Check why flexible height does not work
fun getRelativeDuration(date: Date): Int {
    val currentDate = Date()
    val duration = date.time - currentDate.time
    return (duration / (1000 * 60)).toInt()
}

fun getCardHeightInDp(scheduledTask: ScheduledTask): Dp {
    val relativeDuration =  10//getRelativeDuration(Date(scheduledTask.task.durationStamp))
    val minThreshold  = getRelativeDuration(Date(System.currentTimeMillis() + 15 * 60 * 1000))
    val maxThreshold = getRelativeDuration(Date(System.currentTimeMillis() + 180 * 60 * 1000))

    if (relativeDuration < minThreshold){
         return 90.dp
    }
    else if (relativeDuration > maxThreshold) {
        return 200.dp
    }
    else {
        val finalDp = (1.04*(relativeDuration)+11.42).dp
        return finalDp
    }
}
