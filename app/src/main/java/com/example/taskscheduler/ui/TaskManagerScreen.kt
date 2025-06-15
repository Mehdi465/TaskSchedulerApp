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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Priority
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.ThemeGreen1
import com.example.taskscheduler.ui.viewModel.TaskManagerViewModel
import com.example.taskscheduler.ui.viewModel.TaskViewModelFactory
import java.util.Date
import kotlin.time.Duration


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
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Composable
fun TaskManagerScreen(
    modifier: Modifier,
){

    // --- Get Repository from Application context ---
    val context = LocalContext.current
    // It's good practice to ensure the context is indeed an application context
    // if you are sure it's always called from within an activity/composable with app context.
    val application = context.applicationContext as TaskApplication // Cast to your Application class
    val tasksRepository = application.tasksRepository

    // --- Create the ViewModel using the Factory ---
    val viewModel: TaskManagerViewModel = viewModel(
        factory = TaskViewModelFactory(tasksRepository)
    )

    // --- State for Input Fields ---
    var taskNameInput by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) } // Default priority
    //var priorityMenuExpanded by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf(Duration.ZERO) }

    // --- Observe Task List from ViewModel ---
    val uiState by viewModel.taskListUiState.collectAsStateWithLifecycle() // Use lifecycle-aware collector
    val tasks = uiState.tasks
    val isLoading = uiState.isLoading
    
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

        Box(
            modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(color = Color.DarkGray)
        ){
            Text(
                text= "List of Tasks",
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (tasks.isEmpty()){
            Text("No tasks")
        }
        else if (isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else {
            TaskListBody(
                tasks = tasks,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (showNewTaskDialog) {
            NewTaskDialog(
                viewModel = viewModel,
                selectedPriority = selectedPriority,
                taskNameInput = taskNameInput,
                selectedDuration = selectedDuration,
                onDurationChange = { updatedDuration ->
                    selectedDuration = updatedDuration // Update parent's state when dialog reports change
                },
                onConfirm = { name, priority, durationFromDialog ->
                    // IMPORTANT: Use the 'durationFromDialog' received from the onConfirm callback,
                    // which is the 'currentDuration' from within the dialog's scope.
                    // Or, if onConfirm doesn't pass it, newDialogTaskDuration should be up-to-date.
                    val newTask = Task(
                        name = name,
                        priority = priority,
                        duration = durationFromDialog // Use the confirmed duration
                    )
                    viewModel.addTask(newTask) // Or however your ViewModel takes the task
                    showNewTaskDialog = false
                },
                onNameChange = { taskNameInput = it },
                onPriorityChange = { selectedPriority = it },
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
    viewModel: TaskManagerViewModel,
) {

    var taskToModify by remember { mutableStateOf<Task?>(null) }

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
                onDeleteTask = { task ->
                    viewModel.deleteTask(task)
                },
                onStartModifyTask = { task ->
                    // For now, just print or set state to show a dialog/navigate
                    println("UI: Start modifying task: ${task.name}")
                    taskToModify = task // You would use this to show a dialog or navigate
                },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onDeleteTask: (Task) -> Unit,
    onStartModifyTask: (Task) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = tasks,
            key = {task -> task.id}
        ) { task ->
            SwipableTaskItem(task = task,
                onDelete = { onDeleteTask(task) },
                onModify = { onStartModifyTask(task) }, // Pass the modify callback
                modifier = Modifier
                    .padding(8.dp)
                )
        }
    }
}

@Composable
private fun SwipableTaskItem(task: Task,
                             onDelete: () -> Unit,
                             onModify: () -> Unit,
                             modifier: Modifier = Modifier){


    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> { // Swiped from End (right) to Start (left) -> Delete
                    onDelete()
                    return@rememberSwipeToDismissBoxState true // Confirm dismiss
                }
                SwipeToDismissBoxValue.StartToEnd -> { // Swiped from Start (left) to End (right) -> Modify
                    onModify()
                    // For UI-only, we don't want the item to disappear, so return false.
                    // If you wanted it to dismiss and then show a dialog, you'd return true.
                    return@rememberSwipeToDismissBoxState false // Do not dismiss, just trigger action
                }
                SwipeToDismissBoxValue.Settled -> { // Not dismissed
                    return@rememberSwipeToDismissBoxState false
                }
            }
        },
        // Positional threshold can be adjusted if needed
        // positionalThreshold = { distance -> distance * 0.5f } // Example: 50% swipe needed
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        enableDismissFromStartToEnd = true, // Enable swipe from left to right (Modify)
        enableDismissFromEndToStart = true,   // Enable swipe from right to left (Delete)
        backgroundContent = {
            val direction = dismissState.dismissDirection // Current swipe direction

            // Background for swipe right (Modify)
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2196F3)) // Blue for Modify
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart // Align content to the left
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Modify Icon",
                            tint = Color.White,
                            modifier = Modifier.scale(if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) 1f else 0.75f)
                        )
                        Text(
                            "Modify",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
            // Background for swipe left (Delete)
            else if (direction == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red) // Red for Delete
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd // Align content to the right
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Delete",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Icon",
                            tint = Color.White,
                            modifier = Modifier.scale(if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f)
                        )
                    }
                }
            }
        }
    ) {
        TaskItem(task = task)
    }
}


@Composable
fun TaskItem(task: Task,
            modifier: Modifier = Modifier
) {
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
                    text = "Duration: ${task.duration}",
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


