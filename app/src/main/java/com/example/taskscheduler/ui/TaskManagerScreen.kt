package com.example.taskscheduler.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.viewModel.TaskManagerViewModel
import java.util.Date


object TaskManagerDestination : NavigationDestination {
    override val route = "Task_manager"
    override val titleRes = R.string.task_manager_screen
    const val itemIdArg = "itemId"
    val routeWithArgs = "${route}/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    tasks: List<Task>,
    navigateBack: () -> Unit,
    navigateToTSessioManager: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TaskManagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            TaskTopAppBar(
                title = "Task Manager",
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToTSessioManager,
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
    ){innerPadding ->
        TaskManagerScreen(
            tasks = Task.DEFAULT_TASKS,
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Composable
fun TaskManagerScreen(
    tasks: List<Task>,
    modifier: Modifier,
){
    var showNewTaskDialog by remember { mutableStateOf(false) }
    Column() {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { showNewTaskDialog = true },
                Modifier.widthIn(min = 250.dp)
            ) {
                Text(stringResource(R.string.create_new_task))
            }
        }
        if (tasks.isEmpty()){
            Text("No tasks")
        }
        else {
            TaskListBody(
                tasks = tasks,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (showNewTaskDialog) {
            NewTaskDialog(
                onDismiss = {
                    showNewTaskDialog = false
                    println("Dialog dismissed")
                },
                onSave = {
                    showNewTaskDialog = false

                }
            )
        }
    }
}

@Composable
private fun TaskListBody(
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = "Nothing",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            TaskList(
                tasks = tasks,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = tasks) { task ->
            TaskItem(task = task,
                modifier = Modifier
                    .padding(8.dp)
                )
        }
    }
}

@Composable
private fun TaskItem(task: Task, modifier: Modifier = Modifier){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(80.dp),
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
                    .background(color = Color.Red)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.runner),
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
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Text(
                    text = "Duration: No yet implemented", //${Date(task.durationStamp).hours}h ${Date(task.durationStamp).minutes}m",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            var checked by remember { mutableStateOf(false) }
            Checkbox(
                checked = checked,
                onCheckedChange = {checked = it}, //TODO : make the card change color when clicked,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Red,
                    uncheckedColor = MaterialTheme.colorScheme.error,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    ),

                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

// TODO : Check why flexible height does not work



@Preview
@Composable
fun TaskManagerScreenPreview(){
    TaskManagerScreen(tasks = Task.DEFAULT_TASKS,{},{})
}


