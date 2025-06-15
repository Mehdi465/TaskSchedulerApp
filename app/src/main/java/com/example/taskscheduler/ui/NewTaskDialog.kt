package com.example.taskscheduler.ui

import TimePickerDialog
import android.content.Context
import android.graphics.drawable.Icon
import android.icu.text.ListFormatter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.room.util.TableInfo
import com.example.taskscheduler.ui.HelperDialog.ColorPickerDialog
import com.example.taskscheduler.R
import com.example.taskscheduler.data.Priority
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.HelperDialog.ColorCircle
import com.example.taskscheduler.ui.viewModel.TaskManagerViewModel
import java.util.Date
import kotlin.text.toInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


@Composable
fun NewTaskDialog(
    viewModel: TaskManagerViewModel,
    selectedPriority: Priority,
    taskNameInput : String,
    selectedDuration: Duration,
    onConfirm: (name: String, priority: Priority, duration: Duration) -> Unit,
    onDurationChange : (Duration) -> Unit,
    onNameChange : (String) -> Unit,
    onPriorityChange : (Priority) -> Unit,
    onDismiss : () -> Unit,
    onSave: () -> Unit
) {
    val iconList = listOf(
        R.drawable.pen,
        R.drawable.book,
        R.drawable.language,
        R.drawable.runner
    )

    val listPriority = listOf(
        Priority.LOW,
        Priority.MEDIUM,
        Priority.HIGH,
        Priority.MANDATORY
    )

    // State to control the visibility of the color picker dialog
    var showColorPickerDialog by remember { mutableStateOf(false) }

    // State to hold the selected color (you'll likely use this for your task)
    //var selectedColor by remember { mutableStateOf(Color.Black) }

    var showPicker by remember { mutableStateOf(false) }

    //var selectedDuration_dialog by remember {mutableStateOf(selectedDuration)}

    // color selected for the task
    //var selectedTaskColor by remember { mutableStateOf(Color.LightGray)}


    //var selectedTaskLogo by remember { mutableStateOf(iconList.firstOrNull() ?: R.drawable.pen) }


    Dialog(
        onDismissRequest = onDismiss,
        ) {
        var contentOpacity : Float = 0.95f
        Box(
            modifier = Modifier
                .wrapContentSize() // Or specific size
                .alpha(contentOpacity) // Apply opacity to the content's root
                .background(
                    MaterialTheme.colorScheme.surface, // Or any color you want
                    shape = MaterialTheme.shapes.medium // Optional: give it a shape
                )
                .padding(16.dp)
        ) {
            Column() {
                Text(
                    text = "Create a new Task",
                    color = Color.White
                )

                TextField(
                    value = taskNameInput,
                    onValueChange = onNameChange,
                    label = { Text("Task Name") },
                    singleLine = true
                )
                Row() {
                    Button(
                        onClick = {
                            showColorPickerDialog = true
                        }
                    ) {
                        Text("Pick Color")
                    }
                    ColorCircle(Color.Green, onClick = {},true) }
                //ColorCircle(selectedColor, onClick = {},true) }

                Button(onClick = { showPicker = true }) {
                    Text( if (selectedDuration == Duration.ZERO) "Pick Duration"
                    else "Duration: ${selectedDuration.toComponents { h, m, _, _ -> "${h}h ${m}m" }}")
                }
                IconDropdown(iconList,
                    selectedIcon = R.drawable.pen,
                    //selectedIcon = selectedTaskLogo, // Pass the current state
                    onIconSelected = { newIconId ->
                        //selectedTaskLogo = newIconId // Update the state when an icon is selected
                    }
                )

                PriorityDropdown(
                    priorities = listPriority,
                    selectedPriority = selectedPriority,
                    onPrioritySelected = onPriorityChange,
                    modifier= Modifier,
                )
                // Create and Cancel buttons

                Row() {
                    Button(
                        onClick = {
                            val newTask = Task(
                                name = taskNameInput,
                                priority = selectedPriority,
                                duration = selectedDuration
                            )

                            viewModel.addTask(name = taskNameInput, priority = selectedPriority)
                            

                            onDismiss() // close dialog
                        }
                    ) {
                        Text(
                            text = "Create"
                        )
                    }
                    Button(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = "Cancel"
                        )
                    }
                }
            }
        }
    }

    if (showPicker) {
        TimePickerDialog(
            initialHour = selectedDuration.toComponents { h, _, _, _ -> h.toInt() },
            initialMinute = selectedDuration.toComponents { _, m, _, _ -> m.toInt() },
            onTimeSelected = { h, m ->
                val newDuration = h.hours + m.minutes
                onDurationChange(newDuration)
                showPicker = false
            },
            onDismissRequest = { showPicker = false }
        )
    }

    ColorPickerDialog(
        showDialog = showColorPickerDialog,
        onColorSelected = { color ->
            //selectedColor = color
            showColorPickerDialog = false
        },
        onDismiss = {
            showColorPickerDialog = false
        }
    )
}

@Composable
fun PriorityDropdown(
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
fun IconDropdown(
    icons: List<Int>,                     // List of drawable resource IDs
    selectedIcon: Int,
    onIconSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
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
            Image(
                painter = painterResource(id = selectedIcon),
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
            icons.forEach { iconResId ->
                DropdownMenuItem(
                    text = { /* No text needed */ },
                    onClick = {
                        onIconSelected(iconResId)
                        expanded = false
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = "Icon Option  $iconResId",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
            }
        }
    }
}



