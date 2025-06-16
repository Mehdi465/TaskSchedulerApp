package com.example.taskscheduler.ui.HelperDialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlin.collections.forEach

val availableColors = listOf(
    Color(0xFFE57373), // Light Red
    Color(0xFF81C784), // Light Green
    Color(0xFF64B5F6), // Light Blue
    Color(0xFFFFD54F), // Light Yellow
    Color(0xFFBA68C8), // Light Purple
    Color(0xFF4FC3F7), // Light Cyan
    Color(0xFFF06292), // Light Pink
    Color(0xFFA1887F)  // Light Brown
    // Add more colors here as needed
)


@Composable
fun ColorPickerDialog(
    initialColor: Color,
    showDialog: Boolean,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {

    var currentlySelectedInPicker by remember { mutableStateOf(initialColor) }

    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Column() {
                ColorPickerContent(
                    colors = availableColors,
                    onColorClick = { selectedColor ->
                        currentlySelectedInPicker = selectedColor
                    },
                )
                Row(){
                    Button(onClick = {
                        onDismiss
                    }){
                        Text("Cancel")
                    }
                    Button(onClick = {
                        onColorSelected(currentlySelectedInPicker)
                    }){
                        Text("Select")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerContent(
    colors: List<Color>,
    onColorClick: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface) // Use surface color for background
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Color",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ColorRow(
            colors = colors,
            onColorClick = onColorClick
            )
    }
}

@Composable
fun ColorRow(
    colors : List<Color>,
    onColorClick: (Color) -> Unit
){
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 60.dp), // Define grid cells based on item size
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp) // Add padding around the grid content
    ) {
        items(colors) { color ->
            ColorCircle(
                color = color,
                onClick = {
                    onColorClick(color)
                    //onColorClick(color)
                    //onDismiss() // Dismiss the dialog after selecting a color
                }
            )
        }
    }

}

@Composable
fun ColorCircle(
    color: Color,
    onClick: () -> Unit,
    isShowCaseCircle: Boolean = false // to change size if its the first dialog
) {
    Box(
        modifier = Modifier
            .size(if (isShowCaseCircle) 50.dp else 70.dp) // Size of the color circle
            .padding(5.dp) // Padding around the circle
            .clip(CircleShape) // Clip the Box into a circle shape
            .background(color) // Set the background color
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), CircleShape) // Optional: Add a subtle border
            .clickable { onClick() } // Make the circle clickable
    )
}

