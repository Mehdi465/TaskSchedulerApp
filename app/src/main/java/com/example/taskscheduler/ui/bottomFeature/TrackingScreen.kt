package com.example.taskscheduler.ui.bottomFeature

import android.util.Log
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
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.ui.AppViewModelProvider
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.viewModel.tracking.TaskTrackingViewModel
import kotlin.div
import kotlin.times

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
    var isSession by remember { mutableStateOf(false) }

    val countTask = trackingViewModel.totalTaskDoneCount.collectAsState().value
    val countSession = trackingViewModel.totalSessionCount.collectAsState().value
    val totalDuration = trackingViewModel.totalDuration.collectAsState().value
    val lastWeekSessions = trackingViewModel.lastWeekSessions.collectAsState().value
    val firstSession = trackingViewModel.firstSession.collectAsState().value
    val lastSession = trackingViewModel.lastSession.collectAsState().value

    val mostDoneTask = trackingViewModel.mostDoneTask.collectAsState().value
    val mostDoneTrackedTask = trackingViewModel.mostDoneTaskTracked.collectAsState().value
    val deletedTasks = trackingViewModel.deletedTasks.collectAsState().value

    val listSessionDatas = mutableListOf<@Composable (() -> Unit)>(
        { DashboardTile("TotalSession", "$countSession", "sessions", background = Color.DarkGray) },
        { DashboardTile("Total duration", "%.1f".format((totalDuration*0.001)/(60*60)), "hours", background = Color.DarkGray) },
        { DashboardTile("Task/Sessions", if (countSession == 0) "N/A" else ("%.1f".format(countTask.toDouble()/countSession)), "tasks", background = Color.DarkGray) },
        { DashboardTile("Last week sessions", "${lastWeekSessions.size}", "sessions", background = Color.DarkGray) },
        // TODO add releveant info for first and last sessions
        //{ DashboardTile("First Session",  "${firstSession.startTime}", "sessions", background = Color.DarkGray) },
        //{ DashboardTile("Last Session",  "${lastSession.startTime}", "sessions", background = Color.DarkGray) }
    )
    if (firstSession != null){
        listSessionDatas.add { DashboardTile("First Session",  "${firstSession.startTime}", "sessions", background = Color.DarkGray)  }
    }

    if (lastSession != null){
        listSessionDatas.add { DashboardTile("Last Session",  "${lastSession.startTime}", "sessions", background = Color.DarkGray)  }
    }

    val listTaskDatas= mutableListOf<@Composable (() -> Unit)>(
        { DashboardTile("TotalTasks done", "$countTask", "tasks", background = Color.DarkGray) },
        { DashboardTile("Total duration", "${(totalDuration*0.001)/60}", "mins", background = Color.DarkGray) },
    )

    if (mostDoneTrackedTask != null){
        listTaskDatas.add { DashboardTile("most popular task : ${mostDoneTask!!.name}", "${mostDoneTrackedTask.taskId}", "glasses", background = Color.DarkGray) }
    }

    if (deletedTasks.isNotEmpty()){
        val fourthFirstDeletedTasksName = deletedTasks.take(4).joinToString(separator = ", "){ it.name }
        listTaskDatas.add { DashboardWideTile("Last deleted tasks", fourthFirstDeletedTasksName, "", background = Color.DarkGray) }
    }
    else{
        listTaskDatas.add { DashboardTile("No task deleted", "☺\uFE0F", "", background = Color.DarkGray) }
    }

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
            tiles = if (isSession) listTaskDatas else listSessionDatas
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
fun DashboardGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    spacing: Dp = 12.dp,
    tiles: List<@Composable () -> Unit>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        val processedTiles = mutableListOf<List<@Composable () -> Unit>>()
        val squareTileBuffer = mutableListOf<@Composable () -> Unit>()

        tiles.forEach { tile ->
            // Heuristic to check if a tile is "wide" by looking at its composable type name
            val isWideTile = tile.javaClass.name.contains("DashboardWideTile", ignoreCase = true)

            if (isWideTile) {
                // If we have any pending square tiles, add them to the grid first
                if (squareTileBuffer.isNotEmpty()) {
                    processedTiles.add(squareTileBuffer.toList())
                    squareTileBuffer.clear()
                }
                // Add the wide tile as a row of its own
                processedTiles.add(listOf(tile))
            } else {
                // Add square tile to buffer
                squareTileBuffer.add(tile)
                if (squareTileBuffer.size == columns) {
                    processedTiles.add(squareTileBuffer.toList())
                    squareTileBuffer.clear()
                }
            }
        }

        // Add any remaining square tiles in the buffer
        if (squareTileBuffer.isNotEmpty()) {
            processedTiles.add(squareTileBuffer.toList())
        }


        // Render the processed rows
        processedTiles.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                if (row.size == 1) {
                    // This is a wide tile, let it take the full width
                    Box(modifier = Modifier.fillMaxWidth()) {
                        row.first()()
                    }
                } else {
                    // This is a row of square tiles
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
}

/**
 * square dashboard tile
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

/**
 * A rectangular dashboard tile that is designed to span the full width of the grid.
 */
@Composable
fun DashboardWideTile(
    title: String,
    value: String,
    unit: String,
    background: Color = Color(0xFFE8F5E9)
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp), // Use fillMaxWidth and a fixed or intrinsic height
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(text = unit, fontSize = 16.sp, color = Color.Gray)
            }
        }
    }
}
