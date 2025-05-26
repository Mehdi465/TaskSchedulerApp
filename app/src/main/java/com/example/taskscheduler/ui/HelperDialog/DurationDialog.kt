import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TimePickerDialog(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onTimeSelected: (Int, Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Duration") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberPicker(
                    range = 0..23,
                    selected = hour,
                    onValueChange = { hour = it }
                )
                Text(
                    fontSize = 20.sp,
                    text="h",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically)
                )
                NumberPicker(
                    range = 0..59,
                    selected = minute,
                    onValueChange = { minute = it }
                )
                Text(
                    fontSize = 20.sp,
                    text="min",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(hour, minute)
                onDismissRequest()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NumberPicker(
    range: IntRange,
    selected: Int,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selected)

    LaunchedEffect(selected) {
        listState.animateScrollToItem(selected)
    }

    Box(modifier = Modifier.height(120.dp).width(80.dp)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(range.count()) { index ->
                val value = range.first + index
                Text(
                    text = "$value",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onValueChange(value) },
                    fontSize = if (value == selected) 20.sp else 16.sp,
                    fontWeight = if (value == selected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimePickerDialogPreview() {
    MaterialTheme {
        var showPicker by remember { mutableStateOf(true) }

        if (showPicker) {
            TimePickerDialog(
                initialHour = 2,
                initialMinute = 30,
                onTimeSelected = { hour, minute ->
                    println("Selected time: $hour h $minute min")
                    showPicker = false
                },
                onDismissRequest = {
                    showPicker = false
                }
            )
        }
    }
}