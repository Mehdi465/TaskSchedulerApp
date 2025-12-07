package com.example.taskscheduler.ui.bottomFeature

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.set
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.common.utils.DataUtils
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.Task
import com.example.taskscheduler.data.TaskTracking
import com.example.taskscheduler.ui.AppViewModelProvider
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.theme.Dimens
import com.example.taskscheduler.ui.viewModel.tracking.TaskTrackingViewModel
import com.google.errorprone.annotations.Var
import kotlin.Boolean
import kotlin.div
import kotlin.math.roundToInt
import kotlin.ranges.random
import kotlin.text.toFloat


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

    val allTasks by trackingViewModel.allTasks.collectAsState(initial = emptyList())
    val allTasksTracking by trackingViewModel.allTasksTracking.collectAsState(initial = emptyList())


    val gammaColor = 0.07f

    // test example for labels on the X-axis
    val xLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri")

    val listSessionDatas = mutableListOf<@Composable (() -> Unit)>(
        { DashboardTile(stringResource(R.string.total_session), "$countSession", stringResource(R.string.sessions),
            background = variateColor(gammaColor,Color.DarkGray)) },
        { DashboardTile(stringResource(R.string.total_duration), "%.1f".format((totalDuration*0.001)/(60*60)), stringResource(R.string.hours),
            background = variateColor(gammaColor,Color.DarkGray)) },
        { DashboardTile(stringResource(R.string.task_per_session), if (countSession == 0) "N/A"
            else ("%.1f".format(countTask.toDouble()/countSession)), stringResource(R.string.tasks), background = variateColor(gammaColor,Color.DarkGray)) },
        { DashboardTile(stringResource(R.string.last_week_sessions), "${lastWeekSessions.size}", stringResource(R.string.sessions),
            background = variateColor(gammaColor,Color.DarkGray)) },
    )
    if (firstSession != null){
        listSessionDatas.add { DashboardTile(stringResource(R.string.first_session),  "${firstSession.startTime}",
            stringResource(R.string.sessions), background = variateColor(gammaColor,Color.DarkGray))  }
    }

    if (lastSession != null){
        listSessionDatas.add { DashboardTile(stringResource(R.string.last_session),  "${lastSession.startTime}",
            stringResource(R.string.sessions), background = variateColor(gammaColor,Color.DarkGray))  }
    }

    val listTaskDatas= mutableListOf<@Composable (() -> Unit)>(
        { DashboardTile(stringResource(R.string.total_task_done), "$countTask",
            stringResource(R.string.tasks), background = variateColor(gammaColor,Color.DarkGray)) },
        { DashboardTile(stringResource(R.string.total_duration), "${(totalDuration*0.001)/60}",
            stringResource(R.string.minutes), background = variateColor(gammaColor,Color.DarkGray)) }
    )

    if (mostDoneTrackedTask != null){
        listTaskDatas.add { DashboardTile(stringResource(R.string.most_popular_task)+
                " : ${mostDoneTask!!.name}", "${mostDoneTrackedTask.taskId}", "", background = variateColor(gammaColor,Color.DarkGray)) }
    }

    if (deletedTasks.isNotEmpty()){
        val fourthFirstDeletedTasksName = deletedTasks.take(4).joinToString(separator = ", "){ it.name }
        listTaskDatas.add { DashboardWideTile(stringResource(R.string.last_deleted_tasks),
            fourthFirstDeletedTasksName, "", background = variateColor(gammaColor,Color.DarkGray)) }
    }
    else{
        listTaskDatas.add { DashboardTile(stringResource(R.string.no_task_deleted),
            "☺\uFE0F", "", background = variateColor(gammaColor,Color.DarkGray)) }
    }

    listTaskDatas.add {
        val backgroundColor = variateColor(gammaColor, Color.DarkGray)
        DashboardWideTileGraph(
            title = "",//stringResource(R.string.top_5_most_completed_tasks),
            background = backgroundColor
        ) {
            TopTasksBarChart(
                allTasks = allTasks,
                allTaskTrackings = allTasksTracking,
                backgroundColor = backgroundColor
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(Dimens.skipTopBar))

        CustomSwitchWithText(
            word1 = stringResource(R.string.sessions),
            word2 = stringResource(R.string.tasks),
            checked = isSession,
            onCheckedChange = {isSession = it}
        )

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
fun DashboardGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    spacing: Dp = 12.dp,
    tiles: List<@Composable () -> Unit>
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing)
            .verticalScroll(scrollState),
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
        Spacer(modifier = Modifier.padding(Dimens.skipBottomBar))
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
                    color = Color.LightGray
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
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), // Use fillMaxWidth and a fixed or intrinsic height
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

/**
 * A rectangular dashboard tile designed to host a graph or other custom composable content.
 * It provides a title and a consistent card background.
 *
 * @param title The title to display at the top of the tile.
 * @param background The background color of the tile.
 * @param modifier Modifier for the Card.
 * @param content The composable content (e.g., a chart) to display inside the tile.
 */
@Composable
fun DashboardWideTileGraph(
    title: String,
    background: Color = Color(0xFF333333), // A default dark background
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min), // Height will adapt to its content
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            content()
        }
    }
}

/**
 * Processes task data and displays a bar chart of the top 5 most completed tasks.
 *
 * @param allTasks The list of all Task objects to find names.
 * @param allTaskTrackings The list of all tracking data.
 */
@Composable
fun TopTasksBarChart(
    allTasks: List<Task>,
    allTaskTrackings: List<TaskTracking>,
    backgroundColor: Color
) {
    // select most task done with their names
    val top5TaskTracking = allTaskTrackings.subList(0,5)
    val topTasksData : MutableList<Pair<String,Int>> = mutableListOf() // list pair of name,timeCompleted
    for (taskTracking in top5TaskTracking) {
        for (task in allTasks) {
            if (taskTracking.taskId == task.id) {
                topTasksData.add(Pair(task.name, taskTracking.timesCompleted))
            }
        }
    }

    for (task in allTaskTrackings){
        Log.d("TopTasksBarChart", "Task: ${task.taskId}, Count: ${task.timesCompleted}")
    }

    // show a message if there's no data to display
    if (topTasksData.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), contentAlignment = Alignment.Center
        ) {
            Text("No task data available.", color = Color.Gray)
        }
        return
    }

    val names = topTasksData.map { it.first }
    val values = topTasksData.map { it.second }

    // convert data into YCharts' BarData format
    val barData: List<BarData> = remember(values) {
        values.mapIndexed { index, value ->
            BarData(point = Point(x = index.toFloat(), y = value.toFloat()))
        }
    }

    val yAxisMaxValue = remember(values) { (values.maxOrNull() ?: 0) * 5.2f } // Add 20% padding

    // configure X and Y axes
    var xAxisData = AxisData.Builder()
        .startPadding(20.dp)
        .axisStepSize(50.dp)
        .steps(names.size - 1)
        .bottomPadding(10.dp)
        .axisLabelAngle(20f)
        .labelData { index -> names.getOrElse(index) { "" } }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .build()


    val yAxisData = AxisData.Builder()
        .steps(4) // 4 lines on the Y axis
        .labelAndAxisLinePadding(20.dp)
        .labelData { index ->
            val value = (index.toFloat() / 4f) * yAxisMaxValue
            "%.0f".format(value)
        }
        .axisLineColor(Color.Gray)
        .axisLabelColor(Color.Gray)
        .build()

    // create the final BarChartData object
    val finalBarChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            barWidth = 25.dp,
            paddingBetweenBars = 20.dp,
            isGradientEnabled = true,
        ),
        backgroundColor = backgroundColor
    )

    // display chart
    BarChart(modifier = Modifier
        .height(250.dp)
        .background(color = backgroundColor),
         barChartData = finalBarChartData
        )
}


@Composable
fun BarChartScreen() {
    val names = listOf("Task A", "Task B", "Task C", "Task D", "Task E")
    val values = listOf(10, 20, 30, 15, 25)

    val barData: List<BarData> = remember(values) {
        values.mapIndexed { index, value ->
            BarData(
                point = Point(
                    x = index.toFloat(),
                    y = value.toFloat()
                ),
            )
        }
    }

    val yAxisMaxValue = remember(values) {
        (values.maxOrNull() ?: 0) + 1 // give padding on top
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(40.dp)
        .steps(names.size - 1)
        .bottomPadding(10.dp)
        .axisLabelAngle(25f)
        .labelData { index -> names.getOrElse(index) { "" } }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(0.dp)
        .labelData { index ->
            val value = (index.toFloat() / 5f) * yAxisMaxValue
            "%.0f".format(value)
        }
        .build()

    val barChartData = BarChartData(
        chartData = barData,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            barWidth = 25.dp,
            paddingBetweenBars = 15.dp
        ),
        backgroundColor = MaterialTheme.colorScheme.surface
    )

    // display chart
    BarChart(modifier = Modifier.height(350.dp), barChartData = barChartData)
}



/**
 * param in: gamma from 0 to 1, how the color variate from the baseColor, 0 being the baseColor, 1
 * being random
 */
fun variateColor(gamma: Float = 1f, baseColor:Color): Color {
    // force gamma between 0 and 1
    val clampedGamma = gamma.coerceIn(0f, 1f)

    val threshold : Int = (clampedGamma*255).roundToInt()
    val red = baseColor.red
    val blue = baseColor.blue
    val green = baseColor.green
    val alpha = baseColor.alpha

    val redOffset = (-threshold..threshold).random() / 255f
    val greenOffset = (-threshold..threshold).random() / 255f
    val blueOffset = (-threshold..threshold).random() / 255f
    val alphaOffset = (-threshold..threshold).random() / 255f

    val newRed = (red + redOffset).coerceIn(0f, 1f)
    val newGreen = (green + greenOffset).coerceIn(0f, 1f)
    val newBlue = (blue + blueOffset).coerceIn(0f, 1f)
    val newAlpha = (alpha + alphaOffset).coerceIn(0f, 1f)

    return Color(
        red = newRed,
        green = newGreen,
        blue = newBlue,
        alpha = newAlpha
    )
}

@Preview
@Composable
fun BarChartPreview(){
    BarChartScreen()
}
