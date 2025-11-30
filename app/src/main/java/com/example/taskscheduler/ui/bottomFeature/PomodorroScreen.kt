package com.example.taskscheduler.ui.bottomFeature

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskscheduler.BottomAppScheduleBar
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.TaskTopAppBar
import com.example.taskscheduler.data.PomodoroPhase
import com.example.taskscheduler.data.PomodoroService
import com.example.taskscheduler.data.Session
import com.example.taskscheduler.data.pomodoro.PomodoroViewModel
import com.example.taskscheduler.ui.mainLogicUI.compareTime
import com.example.taskscheduler.ui.navigation.NavigationDestination
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroUiState
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModel
import com.example.taskscheduler.ui.viewModel.sharedSessionPomodoroViewModel.SharedSessionPomodoroViewModelFactory
import java.time.Instant
import java.util.Date
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.taskscheduler.data.pomodoro.PomodoroServiceConnector
import com.example.taskscheduler.ui.mainLogicUI.IconTask


object PomodoroDestination : NavigationDestination {
    override val route = "pomodoro"
    override val titleRes = R.string.pomodoro_screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
                   navigateBack: () -> Unit,
                   navigateToHome: () -> Unit,
                   navigateToTaskManager: () -> Unit,
                   navigateToTrophies: () -> Unit,
                   navigateToSettings: () -> Unit,
                   navigateToTracking: () -> Unit,
                   modifier: Modifier = Modifier,
                   scheduleViewModel: SharedSessionPomodoroViewModel = viewModel(
                       factory = SharedSessionPomodoroViewModelFactory(
                           (LocalContext.current.applicationContext as TaskApplication).activeSessionStore,
                           (LocalContext.current.applicationContext as TaskApplication).taskTrackingRepository,
                           (LocalContext.current.applicationContext as TaskApplication).sessionRepository,
                           (LocalContext.current.applicationContext as TaskApplication).sessionTaskEntryRepository,
                           (LocalContext.current.applicationContext as TaskApplication).tasksRepository,
                           (LocalContext.current.applicationContext as TaskApplication).taskDeletedRepository)
                       )
){
    val uiStateShared by scheduleViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TaskTopAppBar(
                title = stringResource(PomodoroDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                actions = {
                    IconButton(onClick = {
                            navigateToSettings()
                    }){
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppScheduleBar(
                onClickHome = navigateToHome,
                onClickPomodoro = {}, // do nothing
                onClickAddNewTask = navigateToTaskManager,
                onClickTrophies = navigateToTrophies,
                onClickTracking = navigateToTracking
            )
        }
    ) { innerPadding ->
        PomodoroContent(
            sharedUiState = uiStateShared,
            session = uiStateShared.session,
            modifier = Modifier.padding(innerPadding),
        )
    }
}



@Composable
fun PomodoroContent_(
    sharedUiState : SharedSessionPomodoroUiState,
    session: Session?,
    modifier : Modifier
){
    val viewModel: PomodoroViewModel = viewModel()

    PomodoroScreen(
        viewModel = viewModel,
        session = session
    )
}

@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    session: Session?
) {

    val context = LocalContext.current
    // val pomodoroServiceState by PomodoroServiceConnector.serviceStateFlow.collectAsState() // From your service

    // When the screen becomes visible, tell the service to refresh its settings
    LaunchedEffect(Unit) { // Runs once when the Composable enters the composition
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_REFRESH_SETTINGS_AND_STATE
        }
        context.startService(intent) // Ensures service is running and processes the action
    }

    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentTime = Date(Instant.now().toEpochMilli())
        Text(
            text = if (session != null && !session.isSessionFinished() && compareTime(currentTime,session.startTime)) stringResource(R.string.current_task) +": ${session.getCurrentTask()!!.name}"
                    else stringResource(R.string.no_current_task),
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )


        Text(
            text = when (pomodoroState.phase) {
                PomodoroPhase.WORK -> stringResource(R.string.work_time)
                PomodoroPhase.BREAK -> stringResource(R.string.short_break)
                PomodoroPhase.LONG_BREAK -> stringResource(R.string.long_break)
                PomodoroPhase.PAUSED -> stringResource(R.string.paused)
                PomodoroPhase.STOPPED -> stringResource(R.string.stopped)
            },
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = viewModel.formatTime(pomodoroState.timeLeftMillis),
            fontSize = 72.sp,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.startTimer()
                        val startIntent = Intent(context, PomodoroService::class.java).apply {
                        action = PomodoroService.ACTION_START
                    }
                    context.startService(startIntent)
                          },
                enabled = pomodoroState.phase == PomodoroPhase.STOPPED
            ) {
                Text(stringResource(R.string.start))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (pomodoroState.phase == PomodoroPhase.PAUSED) {
                        viewModel.resumeTimer()
                    } else {
                        viewModel.pauseTimer()
                    }
                },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text(if (pomodoroState.phase == PomodoroPhase.PAUSED) stringResource(R.string.resume) else stringResource(R.string.pause))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.skipPhase() },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text(stringResource(R.string.skip))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.stopTimer() },
                enabled = pomodoroState.phase != PomodoroPhase.STOPPED
            ) {
                Text(stringResource(R.string.stop))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.cycles)+"${pomodoroState.totalCycles}"+ stringResource(R.string.current) +"${pomodoroState.currentCycle})",
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge
        )

    }
}

/**
 *
 *          TESTSSS
 *
 *
 *
 */

// Define Colors based on the image
val GreenAccent = Color(0xFF2ECC71)
val RedAccent = Color(0xFFFF6B6B)
val LightGrey = Color(0xFFF0F0F0)
val DarkText = Color(0xFF2D3436)
val SubText = Color(0xFF95A5A6)

// Enum to handle the state logic requested
enum class TimerState {
    Idle, Running, Paused
}

@Composable
fun PomodoroContent(
    sharedUiState : SharedSessionPomodoroUiState,
    session: Session?,
    modifier : Modifier
) {
    val currentTime = Date(Instant.now().toEpochMilli())

    var currentTaskName = "No current task"
    var currentTaskDuration = "No duration"
    var currentTaskicon : String? = Icons.Default.Close.toString()
    var currentTaskColor = Color(0xFF222222)

    // if a session is active
    if (session != null && !session.isSessionFinished() && compareTime(currentTime,session.startTime)){
        currentTaskName = stringResource(R.string.current_task) +
                ": ${session.getCurrentTask()!!.name}"
        currentTaskDuration = "${session.getCurrentTask()!!.duration}"
        currentTaskicon = session.getCurrentTask()!!.task!!.icon
        currentTaskColor = session.getCurrentTask()!!.task!!.color
    }
    val viewModel: PomodoroViewModel = viewModel()
    val context = LocalContext.current

    // When the screen becomes visible, tell the service to refresh its settings
    LaunchedEffect(Unit) { // Runs once when the Composable enters the composition
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_REFRESH_SETTINGS_AND_STATE
        }
        // runs service in background
        context.startService(intent)
    }

    val pomodoroState by viewModel.pomodoroState.collectAsStateWithLifecycle()

    var totalTime = if (pomodoroState.phase == PomodoroPhase.WORK) pomodoroState.workDuration
                    else if (pomodoroState.phase == PomodoroPhase.BREAK) pomodoroState.brakeDuration
                    else //do nothing

    Scaffold(
        //containerColor = Color.White,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // --- TASK CARD ---
            TaskInfoCard(
                taskName = currentTaskName,
                totalTimeStr = currentTaskDuration,
                sessionProgress = "N/A",
                taskIcon = currentTaskicon,
                taskColor = currentTaskColor,
                sessionTimeStr = "${(pomodoroState.timeLeftMillis)/(1000*60)}"
            )

            Spacer(modifier = Modifier.height(50.dp))

            // --- CIRCULAR TIMER ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp) // Size of the ring area
            ) {
                // Determine progress (0.0 to 1.0)
                val progress = pomodoroState.timeLeftMillis.toFloat() / totalTime.toFloat()

                TimerRing(
                    progress = progress,
                    taskColor = currentTaskColor
                    )

                // Text inside the ring
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = viewModel.formatTime(pomodoroState.timeLeftMillis),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGrey
                    )
                    Text(
                        text = stringResource(R.string.cycles)+" ${pomodoroState.totalCycles} "+
                                stringResource(R.string.current) +" ${pomodoroState.currentCycle}",
                        fontSize = 16.sp,
                        color = SubText,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = when (pomodoroState.phase) {
                    PomodoroPhase.WORK -> stringResource(R.string.work_time)
                    PomodoroPhase.BREAK -> stringResource(R.string.short_break)
                    PomodoroPhase.LONG_BREAK -> stringResource(R.string.long_break)
                    PomodoroPhase.PAUSED -> stringResource(R.string.paused)
                    PomodoroPhase.STOPPED -> stringResource(R.string.stopped)
                },
                color = SubText,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            // --- CONTROL BUTTONS ---
            // 4. Three Buttons: Start/Pause logic included
            ControlBar(
                onRedo = {
                    viewModel.skipPhase()
                },
                onTogglePlayPause = {
                    if (pomodoroState.phase == PomodoroPhase.PAUSED) {
                        viewModel.resumeTimer()
                    } else {
                        viewModel.pauseTimer()
                    }
                },
                taskColor = currentTaskColor,
                onStop = {
                    viewModel.stopTimer()
                    //timerState = TimerState.Idle
                    //timeLeft = totalTime.toLong() // Reset time
                },
                currentPomodoroPhase = pomodoroState.phase
            )

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- COMPONENT: Task Card ---
@Composable
fun TaskInfoCard(
    taskName: String,
    totalTimeStr: String,
    taskIcon: String?,
    taskColor: Color,
    sessionProgress: String,
    sessionTimeStr: String
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF444444)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.LightGray.copy(alpha = 0.2f)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(taskColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                IconTask(taskIcon)
                //Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Task Name & Total Time
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = taskName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = totalTimeStr,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            // Session Stats
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = sessionProgress,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = sessionTimeStr,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

// --- COMPONENT: Circular Canvas ---
@Composable
fun TimerRing(
    progress: Float,
    taskColor: Color
) {
    Canvas(modifier = Modifier.size(280.dp)) {
        val strokeWidth = 20.dp.toPx()
        val radius = size.minDimension / 2 - strokeWidth / 2

        // progress circle
        drawCircle(
            color = LightGrey,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        drawArc(
            color = taskColor,
            startAngle = -90f,
            sweepAngle = 360 * progress,
            useCenter = false,
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

// --- COMPONENT: Buttons Row ---
@Composable
fun ControlBar(
    taskColor : Color,
    onRedo: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onStop: () -> Unit,
    currentPomodoroPhase: PomodoroPhase
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallCircleButton(icon = Icons.Default.Refresh, onClick = onRedo)

        val mainButtonColor = if (currentPomodoroPhase == PomodoroPhase.STOPPED ||
            currentPomodoroPhase == PomodoroPhase.PAUSED) RedAccent else taskColor
        val mainIcon = if (currentPomodoroPhase == PomodoroPhase.STOPPED ||
            currentPomodoroPhase == PomodoroPhase.PAUSED) Icons.Default.Menu else Icons.Default.PlayArrow

        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(10.dp, CircleShape, spotColor = mainButtonColor.copy(alpha = 0.5f))
                .background(mainButtonColor, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onTogglePlayPause, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = mainIcon,
                    contentDescription = "Toggle",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 3. Stop Button (Right)
        SmallCircleButton(icon = Icons.Default.Close, onClick = onStop)
    }
}

@Composable
fun SmallCircleButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(LightGrey.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = null, tint = SubText)
        }
    }
}

// Utility to format seconds into MM:SS
fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}

