package com.example.taskscheduler.ui.mainLogicUI


import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.AppViewModelProvider
import com.example.taskscheduler.ui.helperComposable.DeleteTaskDialog
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.theme.taskLighten
import com.example.taskscheduler.ui.viewModel.taskmanager.TaskListUiState
import com.example.taskscheduler.ui.viewModel.taskmanager.TaskManagerViewModel
import com.example.taskscheduler.ui.viewModel.taskmanager.TaskViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



object TaskManagerDestination : NavigationDestination {
    override val route = "task_manager"
    override val titleRes = R.string.task_manager_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    navigateBack: () -> Unit,
    navigateToTSessionManager: (selectedTaskIdsString: String) -> Unit,
    navigateToNewTaskScreen: () -> Unit,
    navigateToModifyTaskScreen: (Int) -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: TaskManagerViewModel = viewModel(factory = AppViewModelProvider.Factory)
    ) {

    // Snackbar
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val message = stringResource(R.string.no_tasks_selected)
    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaskTopAppBar(
                title = stringResource(R.string.task_manager),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val selectedTasks = viewModel.getSelectedTasks()

                    if (selectedTasks.isNotEmpty()) { // Optional: Check if any tasks are selected
                        val selectedTaskIdsString = selectedTasks.map { it.id }.joinToString(",")
                        navigateToTSessionManager(selectedTaskIdsString)
                    } else {
                        showNoTaskSelected(snackbarHostState,coroutineScope, message = message)
                    }
                },
                modifier = Modifier
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Create Session"
                )
            }
        }
    ){innerPadding ->
        TaskManagerScreen(
            modifier = Modifier.padding(innerPadding),
            navigateToNewTaskScreen = navigateToNewTaskScreen,
            navigateToModifyTaskScreen = navigateToModifyTaskScreen
        )
    }
}

fun showNoTaskSelected(snackbarHostState: SnackbarHostState,
                   coroutineScope: CoroutineScope, message:String
) {
    coroutineScope.launch {
        snackbarHostState.showSnackbar(
            message = message,
            duration = SnackbarDuration.Short
        )
    }
}


@Composable
fun TaskManagerScreen(
    modifier: Modifier,
    navigateToNewTaskScreen : () -> Unit,
    navigateToModifyTaskScreen: (Int) -> Unit,
){

    // --- Get Repository from Application context ---
    val context = LocalContext.current
    val application = context.applicationContext as TaskApplication // Cast to your Application class
    val tasksRepository = application.tasksRepository
    val taskDeletedRepository = application.taskDeletedRepository
    val taskTrackingRepository = application.taskTrackingRepository

    // --- Create the ViewModel using the Factory ---
    val viewModel: TaskManagerViewModel = viewModel(
        factory = TaskViewModelFactory(
            tasksRepository,
            taskDeletedRepository,
            taskTrackingRepository
        )
    )

    // --- Observe Task List from ViewModel ---
    val uiState by viewModel.taskListUiState.collectAsStateWithLifecycle() // Use lifecycle-aware collector
    val tasks = uiState.tasks
    val isLoading = uiState.isLoading

    // UI task background colors
    val taskBackgroundColors = MutableList(tasks.size){Color.LightGray}

    Column() {


        Spacer(modifier = Modifier.padding(Dimens.spaceM))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = navigateToNewTaskScreen,
                Modifier.widthIn(min = 250.dp)
            ) {
                Text(stringResource(R.string.create_new_task))
            }
        }

//        Box(
//            modifier = Modifier
//                .height(150.dp)
//                .fillMaxWidth()
//                .background(color = Color.DarkGray)
//        ){
//            var checked by remember { mutableStateOf(false) }
//            val tasksCheckId = uiState.tasks
//            Switch(
//                checked = checked,
//                onCheckedChange = {
//                    if (!checked){
//                        tasksCheckId.forEach {
//                            viewModel.toggleTaskSelectionSwitchButton(it.id)
//                        }
//                    }
//                    else {
//                        viewModel.clearSelections()
//                    }
//                    checked = it
//                }
//            )
//
//            Text(
//                text= stringResource(R.string.list_of_task),
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text= stringResource(R.string.list_of_task),
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            // for
            var checked by remember { mutableStateOf(false) }
            val tasksCheckId = uiState.tasks


            Switch(
                checked = checked,
                onCheckedChange = {
                    if (!checked){
                        tasksCheckId.forEach {
                            viewModel.toggleTaskSelectionSwitchButton(it.id)
                        }
                    }
                    else {
                        viewModel.clearSelections()
                    }
                    checked = it
                }
            )
        }

        if (tasks.isEmpty()){
            Text(stringResource(R.string.no_task))
        }
        else if (isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else {
            TaskListBody(
                uiState = uiState,
                tasks = tasks,
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                navigateToModifyTaskScreen = navigateToModifyTaskScreen
            )
        }
    }
}

@Composable
private fun TaskListBody(
    uiState: TaskListUiState,
    tasks: List<Task>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    viewModel: TaskManagerViewModel,
    navigateToModifyTaskScreen: (Int) -> Unit,
) {

    var taskToModify by remember { mutableStateOf<Task?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        if (tasks.isEmpty()) {
            Text(
                text = stringResource(R.string.nothing),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            TaskList(
                uiState = uiState,
                onDeleteTask = { task ->
                    viewModel.deleteTask(task)
                },
                onStartModifyTask = { task ->
                    taskToModify = task
                    navigateToModifyTaskScreen(task.id)
                },
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = 8.dp),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun TaskList(
    uiState: TaskListUiState,
    onDeleteTask: (Task) -> Unit,
    onStartModifyTask: (Task) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: TaskManagerViewModel
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(
            items = uiState.tasks,
            key = {task -> task.id}
        ) { task ->
            SwipableTaskItem(task = task,
                isSelected = task.id in uiState.checkedTasks,
                onDelete = { onDeleteTask(task) },
                onModify = { onStartModifyTask(task) },
                modifier = Modifier
                    .padding(Dimens.spaceXXS),
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun SwipableTaskItem(task: Task,
                             isSelected: Boolean,
                             onDelete: () -> Unit,
                             onModify: () -> Unit,
                             modifier: Modifier = Modifier,
                             viewModel: TaskManagerViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    var isSwipeBoxVisible by remember { mutableStateOf(true) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.EndToStart -> { // Swiped from End (right) to Start (left) -> Delete
                    if (!showDeleteDialog) {
                        showDeleteDialog = true
                    }
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
        positionalThreshold = { distance -> distance * 0.7f } // Example: 50% swipe needed
    )

    LaunchedEffect(showDeleteDialog) {
        if (!showDeleteDialog && !isSwipeBoxVisible) {
            isSwipeBoxVisible = true
        }
    }

    // make the tile task swipabale again
    if (showDeleteDialog) {
        DeleteTaskDialog(
            onDismiss = { // User clicked "Cancel" or dismissed the dialog
                showDeleteDialog = false
                coroutineScope.launch {
                    isSwipeBoxVisible = false // Remove from composition
                    delay(50) // Brief delay to ensure recomposition happens
                    isSwipeBoxVisible = true
                }
            },
            onConfirm = { // User clicked "Delete"
                showDeleteDialog = false
                onDelete()
            }
        )
    }

    Crossfade(targetState = isSwipeBoxVisible, label = "SwipeBoxVisibility") { visible ->
        if (visible) {
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
                                    contentDescription = stringResource(R.string.modifiy),
                                    tint = Color.White,
                                    modifier = Modifier.scale(if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) 1.25f else 0.75f)
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
                    // Background for swipe left for Delete
                    else if (direction == SwipeToDismissBoxValue.EndToStart) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Red) // red for Delete
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
                                    contentDescription = stringResource(R.string.delete),
                                    tint = Color.White,
                                    modifier = Modifier.scale(if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1.25f else 0.75f)
                                )
                            }
                        }
                    }
                }
            ) {
                var cardColor by remember { mutableStateOf(task.color.taskLighten()) }
                TaskItem(
                    backgroundColor = cardColor,
                    task = task,
                    isSelected = isSelected,
                    viewModel = viewModel,
                    onColorChange = { newColor ->
                        cardColor = newColor
                    }
                )
            }
        }
    }
}


@Composable
fun TaskItem(
             backgroundColor : Color,
             task: Task,
             isSelected: Boolean = true,
             viewModel: TaskManagerViewModel,
             onColorChange: (Color) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .height(80.dp),
            colors = CardDefaults.cardColors(
            containerColor = if(isSelected) backgroundColor else Color.LightGray
        )
    ) {
        Row(
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .background(color = task.color)
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                if (task.icon != null) {
                    IconTask(task.icon)
                } else {
                    Spacer(modifier = Modifier
                        .width(32.dp)
                        .height(32.dp))
                }
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
                    color = Color.DarkGray
                )

                Text(
                    text = stringResource(R.string.duration)+": ${task.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Text(
                    text = stringResource(R.string.priority)+": ${task.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        Log.d("TaskItem", "Checkbox clicked for task ${isSelected}")
                        viewModel.toggleTaskSelection(task.id)
                        onColorChange(if (isSelected) Color.LightGray else task.color.taskLighten())
                                      },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = task.color,
                        checkedColor = task.color,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    ),

                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
        }
    }
}
