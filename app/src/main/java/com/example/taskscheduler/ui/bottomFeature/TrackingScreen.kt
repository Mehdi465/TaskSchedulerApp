package com.example.taskscheduler.ui.bottomFeature

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens

object TrackingDestination : NavigationDestination {
    override val route = "monitoring"
    override val titleRes = R.string.tracking_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToPomodoro: () -> Unit,
    navigateToTaskManager: () -> Unit,
    navigateToTrophies: () -> Unit
) {
    Scaffold(
    topBar = {
        TaskTopAppBar(
            title = stringResource(TrackingDestination.titleRes),
            canNavigateBack = true,
            navigateUp = navigateBack
        )
    },
    bottomBar = {
        BottomAppScheduleBar(
            onClickHome = navigateToHome,
            onClickPomodoro = navigateToPomodoro,
            onClickAddNewTask = navigateToTaskManager,
            onClickTrophies = navigateToTrophies
        )
    }
    )
    { innerPadding ->
        TrackingContent(
            modifier = Modifier.padding(innerPadding),
        )
    }
}


@Composable
fun TrackingContent(
    modifier: Modifier = Modifier
){
    var isSession by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(Dimens.skipTopBar))

        CustomSwitchWithText(
            word1 = "Session",
            word2 = "Task",
            checked = isSession,
            onCheckedChange = {isSession = it}
        )

        GraphBox(
            isTaskMode = isSession,
        )

        // test for SquareTilesColumn
        val data = (1..7).map{"Item $it"}
        SquareTilesColumn(items = data, columns = 3, spacing = 10.dp) { item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFBBDEFB)),
                contentAlignment = Alignment.Center
            ) {
                Text(item)
            }
        }
    }
}

@Composable
fun CustomSwitchWithText(
    word1: String,
    word2: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val selectedColor = Color(0xFF4CAF50)
    val unselectedColor = Color.Gray.copy(alpha = 0.5f)
    val selectedTextColor = Color.White
    val unselectedTextColor = Color.Black

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(24.dp))
            .background(unselectedColor)
            .padding(2.dp)
            .clickable {
                // Clicking the container toggles the state
                onCheckedChange(!checked)
            }
    ) {
        // "Word 1" button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(if (!checked) selectedColor else Color.Transparent)
                .clickable {
                    if (checked) {
                        onCheckedChange(false)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word1,
                color = if (!checked) selectedTextColor else unselectedTextColor,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }

        // "Word 2" button
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(24.dp))
                .background(if (checked) selectedColor else Color.Transparent)
                .clickable {
                    if (!checked) {
                        onCheckedChange(true)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = word2,
                color = if (checked) selectedTextColor else unselectedTextColor,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GraphBox(
    isTaskMode: Boolean,
){
    Box() {
        Text("Currently ")
    }
}

@Composable
fun DrawCircleGraphWithIcon(value: Int,
                            color: Color,
                            remainingPartColor: Color = Color.Blue,
                            iconDrawable: Int? = null,
                            text : String? = null,
                            contentColor : Color = Color.Red,
                            backgroundColor : Color = Color.DarkGray) {

    var radius by remember { mutableFloatStateOf(0f) }
    var canvasWidth by remember { mutableFloatStateOf(0f) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasWidth = size.width
            val canvasHeight = size.height

            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f
            radius = canvasWidth / 2.3f //arrange circle size



            val sweepAngle = (value / 100f) * 360f

            // Draw the incomplete part of the circle
            drawArc(
                color = remainingPartColor,
                startAngle = sweepAngle,
                sweepAngle = 360f - sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius/9f)//arrange stroke size according to radius
            )

            // Draw the completed part of the circle
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = radius/6f)//arrange stroke size according to radius
            )
        }

        Column(modifier = Modifier.height(canvasWidth.dp).width(canvasWidth.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            if(iconDrawable != null){
                Icon(
                    painter = painterResource(id = iconDrawable),
                    contentDescription = null,
                    modifier = Modifier
                        .size((radius / 4).dp),//arrange stroke size according to radius
                    tint = color

                )
            }


            if(text != null){
                if (contentColor != null) {
                    Text(text, fontSize = (radius / 16).sp,
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = (radius / 24).dp))//arrange stroke size according to radius
                }
            }

        }

    }
}


///**
// * Grid of square tiles using LazyVerticalGrid.
// *
// * @param items list of data items
// * @param columns number of columns in the grid
// * @param spacing spacing between tiles
// * @param tileContent composable used to display each tile (receives the data item)
// */
//@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
//@Composable
//fun <T> SquareTileGrid(
//    items: List<T>,
//    columns: Int = 2,
//    spacing: Dp = 8.dp,
//    modifier: Modifier = Modifier,
//    tileContent: @Composable BoxScope.(T) -> Unit
//) {
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(columns),
//        modifier = modifier.fillMaxSize(),
//        contentPadding = PaddingValues(spacing),
//        horizontalArrangement = Arrangement.spacedBy(spacing),
//        verticalArrangement = Arrangement.spacedBy(spacing)
//    ) {
//        items(items) { item ->
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    // make this cell square
//                    .aspectRatio(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                tileContent(item)
//            }
//        }
//    }
//}

@Composable
fun ExampleTile(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.Black)
    }
}

/**
 * Manual Column that creates rows of square tiles.
 * Useful where you prefer explicit Column/Row structure or custom row-level decorations.
 */
@Composable
fun <T> SquareTilesColumn(
    items: List<T>,
    columns: Int = 2,
    spacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    tileContent: @Composable BoxScope.(T) -> Unit
) {
    val rows = items.chunked(columns)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                // equal weight per column so each tile gets same width
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            // keep square by using aspect ratio 1
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        tileContent(item)
                    }
                }

                // If the last row has fewer items than columns, fill the remaining space
                val missing = columns - rowItems.size
                if (missing > 0) {
                    repeat(missing) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}