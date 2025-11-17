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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.ui.AppViewModelProvider
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.viewModel.tracking.TaskTrackingViewModel

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
    navigateToTrophies: () -> Unit,
    trackingViewModel: TaskTrackingViewModel = viewModel(factory = AppViewModelProvider.Factory)
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
            trackingViewModel = trackingViewModel
        )
    }
}


@Composable
fun TrackingContent(
    modifier: Modifier = Modifier,
    trackingViewModel: TaskTrackingViewModel
){
    var isSession by remember { mutableStateOf(true) }

    val countTask = trackingViewModel.totalTaskDoneCount.collectAsState().value
    val countSession = trackingViewModel.totalSessionCount.collectAsState().value
    val totalDuration = trackingViewModel.totalDuration.collectAsState().value

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
        DashboardGrid(
            modifier = Modifier.padding(16.dp),
            columns = 2,
            spacing = 16.dp,
            tiles = listOf(
                { DashboardTile("TotalT", "$countTask", "tasks", background = Color.DarkGray) },
                { DashboardTile("TotalS", "$countSession", "sessions", background = Color.DarkGray) },
                { DashboardTile("Total duration", "${(totalDuration*0.001)/60}", "mins", background = Color.DarkGray) },
                { DashboardTile("Water", "12", "glasses", background = Color.DarkGray) }
            )
        )
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


@Composable
fun DashboardGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    spacing: Dp = 12.dp,
    tiles: List<@Composable () -> Unit>
) {
    val rows = tiles.chunked(columns)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                row.forEach { tile ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f) // ensures square
                    ) {
                        tile()
                    }
                }

                // Fill remaining space if row is incomplete
                if (row.size < columns) {
                    repeat(columns - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Example square dashboard tile
 */
@Composable
fun DashboardTile(
    title: String,
    value: String,
    unit: String,
    background: Color = Color(0xFFE8F5E9)
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, fontSize = 14.sp, color = Color.Gray)
            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(text = unit, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
