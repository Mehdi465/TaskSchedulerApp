package com.example.taskscheduler.ui

import TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Priority
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.Task.Companion.IconMap
import com.example.taskscheduler.ui.HelperDialog.ColorCircleMenu
import com.example.taskscheduler.ui.HelperDialog.ColorPickerDialog
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.viewModel.NewTaskViewModel
import com.example.taskscheduler.ui.viewModel.NewTaskViewModelFactory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object NewTaskScreenDestination : NavigationDestination {
    override val route = "New_task"
    override val titleRes = R.string.new_task_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(
    onDismiss : () -> Unit,
    canNavigateBack: Boolean = true,
){
    Scaffold(
        modifier = Modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(R.string.new_task_screen),
                canNavigateBack = canNavigateBack,
                navigateUp = onDismiss
            )
        },
    ) {innerPadding ->
        NewTaskContent(
            modifier = Modifier.padding(innerPadding),
            onDismiss = onDismiss
        )
    }
}



@Composable
fun NewTaskContent(
    modifier: Modifier,
    onDismiss : () -> Unit,
) {
    val listPriority = listOf(
        Priority.LOW,
        Priority.MEDIUM,
        Priority.HIGH,
        Priority.MANDATORY
    )

    // --- Get Repository from Application context ---
    val context = LocalContext.current
    val application = context.applicationContext as TaskApplication // Cast to your Application class
    val tasksRepository = application.tasksRepository

    // --- Create the ViewModel using the Factory ---
    val viewModel: NewTaskViewModel = viewModel(
        factory = NewTaskViewModelFactory(tasksRepository)
    )

    // --- State for Input Fields ---
    var taskNameInput by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) }
    var selectedDuration by remember { mutableStateOf(Duration.ZERO) }
    var selectedColor by remember { mutableStateOf(Color(0xFFE57373)) }
    var selectedIcon by remember { mutableStateOf("pen") }

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxSize()
    ) {

        Spacer(modifier = Modifier
            .width(32.dp)
            .height(85.dp)
        )

        // max input char for textfield
        val maxChar : Int = 40
        TextField(
            value = taskNameInput,
            onValueChange = {
                if (it.length <= maxChar) {
                    taskNameInput = it
                }
                            },
            label = { Text(stringResource(R.string.task_name)) },
            singleLine = true
        )
        Row() {
            Button(
                onClick = {
                    showColorPickerDialog = true
                }
            ) {
                Text(stringResource(R.string.pick_color))
            }
            ColorCircleMenu(selectedColor, onClick = {}, true)
        }
        Button(onClick = { showTimePicker = true }) {
            Text(
                if (selectedDuration == Duration.ZERO) stringResource(R.string.pick_duration)
                else "Duration: ${selectedDuration.toComponents { h, m, _, _ -> "${h}h ${m}m" }}"
            )
        }

        IconDropdownScreen(
            IconMap.drawableMap.keys.toList(),
            selectedIcon = selectedIcon,
            onIconSelected = { selectedIcon = it },
        )

        PriorityDropdownScreen(
            priorities = listPriority,
            selectedPriority = selectedPriority,
            onPrioritySelected = { selectedPriority = it },
            modifier = Modifier,
        )
        // Create and Cancel buttons

        Row() {
            Button(
                onClick = {
                    val newTask = Task(
                        name = taskNameInput,
                        priority = selectedPriority,
                        duration = selectedDuration,
                        color = selectedColor,
                        icon = selectedIcon
                    )
                    viewModel.addTask(newTask)
                    onDismiss() // close dialog
                }
            ) {
                Text(
                    text = stringResource(R.string.create)
                )
            }
            Button(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }
    }



    if (showTimePicker) {
        TimePickerDialog(
            initialHour = selectedDuration.toComponents { h, _, _, _ -> h.toInt() },
            initialMinute = selectedDuration.toComponents { _, m, _, _ -> m.toInt() },
            onTimeSelected = { h, m ->
                val newDuration = h.hours + m.minutes
                selectedDuration = newDuration
                showTimePicker = false
            },
            onDismissRequest = { showTimePicker = false }
        )
    }

    ColorPickerDialog(
        showDialog = showColorPickerDialog,
        initialColor = selectedColor,
        onColorSelected = { newColor ->
            selectedColor = newColor
            showColorPickerDialog = false
        },
        onDismiss = {
            showColorPickerDialog = false
        }
    )
}


@Composable
fun PriorityDropdownScreen(
    priorities: List<Priority>,
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit,
    modifier: Modifier = Modifier,
){
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .wrapContentSize(Alignment.TopStart)
        .background(color = Color.White)
        .zIndex(1f)
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text=selectedPriority.toString(), color = Color.Black)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.zIndex(2f),
            containerColor = Color.White
        ) {
            priorities.forEach { prioRes ->
                DropdownMenuItem(
                    text = { Text(prioRes.toString(), color = Color.Black) },
                    onClick = {
                        onPrioritySelected(prioRes)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun IconDropdownScreen(
    icons: List<String>,
    selectedIcon: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopStart)
            .background(color = Color.White)
            .zIndex(1f)
    ) {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(IconMap.getIconResId(selectedIcon)),
                contentDescription = "Selected Icon",
                modifier = Modifier.size(32.dp)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.zIndex(2f),
            containerColor = Color.White
        ) {
            icons.forEach { iconResName ->
                DropdownMenuItem(
                    text = { /* No text needed */ },
                    onClick = {
                        onIconSelected(iconResName)
                        expanded = false
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(IconMap.getIconResId(iconResName)),
                            contentDescription = "Icon Option  $iconResName",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun NewTaskScreenPreview() {
    NewTaskScreen({})
}



