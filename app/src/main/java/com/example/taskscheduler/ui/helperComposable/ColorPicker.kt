package com.example.taskscheduler.ui.helperComposable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.taskscheduler.R
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.theme.TaskColors

// import from Color.kt
val availableColors = TaskColors

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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(Dimens.spaceS)
                ) {
                    ColorPickerContent(
                        colors = availableColors,
                        onColorClick = { selectedColor ->
                            currentlySelectedInPicker = selectedColor
                        },
                        currentlySelectedInPicker = currentlySelectedInPicker
                    )

                    Spacer(modifier = Modifier.height(Dimens.spaceS))

                    Row(
                        modifier = Modifier
                            .padding(horizontal = Dimens.spaceS),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            onDismiss()
                        }) {
                            Text(stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.padding(Dimens.spaceS))

                        Button(onClick = {
                            onColorSelected(currentlySelectedInPicker)
                        }) {
                            Text(stringResource(R.string.select))
                        }
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
    onColorClick: (Color) -> Unit,
    currentlySelectedInPicker : Color
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface) // Use surface color for background
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.select_color),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        ColorRow(
            colors = colors,
            onColorClick = onColorClick,
            currentlySelectedInPicker =currentlySelectedInPicker
        )
    }
}

@Composable
fun ColorRow(
    colors : List<Color>,
    onColorClick: (Color) -> Unit,
    currentlySelectedInPicker: Color
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
                },
                isSelected = color == currentlySelectedInPicker,
            )
        }
    }
}

@Composable
fun ColorCircle(
    color: Color,
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    isShowCaseCircle: Boolean = false
) {
    val circleSize = if (isShowCaseCircle) 50.dp else 70.dp
    val baseBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    val selectedBorderColor = MaterialTheme.colorScheme.primary
    val selectedBorderWidth = 2.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(circleSize)
            .padding(5.dp)
            .clip(CircleShape)
            .background(
                color = color
            )
            .border(
                width = if (isSelected) selectedBorderWidth else 1.dp,
                color = if (isSelected) selectedBorderColor else baseBorderColor,
                shape = CircleShape
            )
            .clickable { onClick() }

    ) {
        // Option 3: Show a check icon when selected
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimaryContainer, // A color that contrasts well with most selected colors
                // Or calculate a contrasting color based on 'color'
                modifier = Modifier.size(circleSize * 0.5f) // Adjust size as needed
            )
        }
    }
}

@Composable
fun ColorCircleMenu(
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