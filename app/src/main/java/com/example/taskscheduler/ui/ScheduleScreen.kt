package com.example.taskscheduler.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.paddingFrom
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.taskscheduler.data.ScheduledTask
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.ui.navigation.NavigationDestination
import java.util.Date
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSreen(
    navigateToTaskManager: () -> Unit,
    modifier: Modifier = Modifier,
    session: Session
){
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
        TimelineScreen(
            session = session,
            modifier = Modifier.padding(innerPadding),
            )
    }
}

@Composable
fun TimelineScreen(session: Session,
                   modifier: Modifier = Modifier,
) {
    var tasks by remember { mutableStateOf(session.tasks)}//sortedBy { it.task.durationStamp }) }

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
                text = "${getHoursString(scheduledTask.startDate)} : ${getMinutesString(scheduledTask.startDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                modifier = Modifier.padding(bottom = 8.dp),
                text = "${getHoursString(scheduledTask.endDate)} : ${getMinutesString(scheduledTask.endDate)}",
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
                    .fillMaxHeight() // Fill the height of the Row
                    .width(56.dp) // Give it a fixed width (adjust as needed)
                    .background(color = Color.Red)//Color(scheduledTask.task.getColor())) // Set the background color from the task
                    .padding(8.dp) // Add some padding inside the box
                    .align(Alignment.CenterVertically), // Align this box vertically in the center of the Row
                contentAlignment = Alignment.Center // Center the content (the icon) within the Box
            ) {
                Image(
                    painter = painterResource(R.drawable.pen),
                    contentDescription = "Task logo",
                    modifier = Modifier
                        .width(32.dp)
                        .height(32.dp)
                )
            }

            // The existing Column for task details
            Column(
                modifier = Modifier
                    .weight(1f) // Take up the remaining width
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp) // Add padding
                    .align(Alignment.CenterVertically)
            ) {

                Text(
                    text = scheduledTask.name,
                    style = MaterialTheme.typography.titleMedium, // Use a MaterialTheme typography style
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = "Duration: Not yet implemented",//${Date(scheduledTask.task.durationStamp).hours}h ${Date(scheduledTask.task.durationStamp).minutes}m", // Display duration in minutes
                    style = MaterialTheme.typography.bodySmall, // Use a smaller typography style
                    color = Color.Gray // Example: Gray text for duration
                )
                Text(
                    text = "From: ${scheduledTask.startDate.hours}h ${scheduledTask.startDate.minutes}m - To: ${scheduledTask.endDate.hours}h ${scheduledTask.endDate.minutes}m", // Use a locale-aware date format
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

@Preview
@Composable
fun ScheduleScreenPreview(){
    ScheduleSreen(
        session = Session.sessionWithDefaults,
        navigateToTaskManager = {})
}

/*
@Preview
@Composable
fun ScheduleScreenPreview(){
    TimelineScreen(session = Session.sessionWithDefaults)
}*/
/*
@Preview
@Composable
fun TaskItemPreview(){
    TaskItem(scheduledTask = ScheduledTask.REVIEW_TASK_SCHEDULED)
}
*/

/*
@Preview
@Composable
fun TaskCardPreview(){
    TaskCard(
        ScheduledTask(task = Task.EXECUTION_TASK, Date(), Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY,1 ) }.time))
}
*/