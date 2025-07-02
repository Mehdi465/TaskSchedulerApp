package com.example.taskscheduler.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.taskscheduler.ui.HelperDialog.InfiniteTimePickerWheel
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
        horizontalAlignment = Alignment.CenterHorizontally,
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

        Spacer(modifier = Modifier.padding(8.dp))

        Row() {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                ),
                onClick = {
                    showColorPickerDialog = true
                }
            ) {
                Text(stringResource(R.string.pick_color))
            }
            ColorCircleMenu(selectedColor, onClick = {}, true)
        }


        DurationSection(
            themeColor = selectedColor,
            initialHour = selectedDuration.toComponents { h, _, _, _ -> h.toInt() },
            initialMinute = selectedDuration.toComponents { _, m, _, _ -> m.toInt() },
            onTimeSelected = { h, m ->
                val newDuration = h.hours + m.minutes
                selectedDuration = newDuration
                showTimePicker = false
            },
        )

        IconSection(
            themeColor = selectedColor,
            icons = IconMap.drawableMap.keys.toList(),
            selectedIcon = selectedIcon,
            onIconSelected = { selectedIcon = it },
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // priority section
        PrioritySection(
            themeColor = selectedColor,
            priorities = listPriority,
            onPrioritySelected = { selectedPriority = it },
        )

        Spacer(modifier = Modifier.padding(8.dp))

        // Create and Cancel buttons
        Row() {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                ),
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

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                ),
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }
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
fun DurationSection(
    themeColor: Color,
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
){
    Column() {
        Text(
            text = stringResource(R.string.duration),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        InfiniteTimePickerWheel(
            themeColor = themeColor,
            initialHour = initialHour,
            initialMinute = initialMinute,
            onTimeSelected = onTimeSelected
        )
    }
}

@Composable
fun IconSection(
    modifier: Modifier = Modifier,
    themeColor: Color,
    icons: List<String>,
    selectedIcon: String = icons[0],
    onIconSelected: (String) -> Unit
){
    Column() {

        Text(
            text = stringResource(R.string.icons),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        HorizontalIconWheel(
            themeColor = themeColor,
            icons = icons,
            selectedIcon = selectedIcon,
            onIconSelected = onIconSelected
        )
    }
}

@Composable
fun PrioritySection(
    modifier: Modifier = Modifier,
    themeColor: Color,
    priorities: List<Priority>,
    onPrioritySelected : (Priority) -> Unit,
){
    Column() {

        Text(
            text = stringResource(R.string.priority),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        PrioritySelector(
            themeColor = themeColor,
            priorities = priorities,
            onPrioritySelected = onPrioritySelected
        )
    }
}



@Composable
fun HorizontalIconWheel(
    themeColor: Color,
    icons: List<String>,
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    val selectedIndex = icons.indexOf(selectedIcon)
    var selected by remember { mutableStateOf(selectedIndex) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        // Left Arrow Indicator
        Icon(
            imageVector = Icons.Default.KeyboardArrowLeft,
            contentDescription = "Left",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp)
                .size(24.dp)
                .alpha(0.6f),
            tint = Color.White
        )

        // Icon Wheel
        Row(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .horizontalScroll(scrollState)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2C2C2C))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            icons.forEachIndexed { index, icon ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) themeColor else Color(0xFF3C3C3C))
                        .clickable {
                            selected = index
                            onIconSelected(icons[index])
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(IconMap.getIconResId(icon)),
                        contentDescription = "Icon $index",
                        //tint = if (isSelected) Color.Black else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Right Arrow Indicator
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Right",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp)
                .size(24.dp)
                .alpha(0.6f),
            tint = Color.White
        )
    }
}

@Composable
fun PrioritySelector(
    themeColor: Color,
    priorities: List<Priority>,
    onPrioritySelected: (Priority) -> Unit
) {
    var selected by remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(Color(0xFF2C2C2C), RoundedCornerShape(16.dp)),
        //.fillMaxWidth()
        //.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        priorities.forEachIndexed { index, label ->
            val isSelected = index == selected

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) themeColor else Color(0xFF3C3C3C)
                    )
                    .clickable {
                        selected = index
                        onPrioritySelected(priorities[index])
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label.toString(),
                    color = if (isSelected) Color.Black else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}




@Composable
@Preview
fun NewTaskScreenPreview() {

}



