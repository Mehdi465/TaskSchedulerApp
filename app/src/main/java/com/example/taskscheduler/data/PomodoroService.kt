package com.example.taskscheduler.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.taskscheduler.MainActivity
import com.example.taskscheduler.R
import com.example.taskscheduler.TaskApplication
import com.example.taskscheduler.data.pomodoro.PomodoroServiceConnector
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

//constants for Pomodoro durations (in milliseconds)
private val WORK_DURATION_MILLIS = 25 * 60 * 1000L // 25 minutes
private val BREAK_DURATION_MILLIS = 5 * 60 * 1000L // 5 minutes

// defaults values if datastore is empty
val DEFAULT_WORK_DURATION = 25L
val DEFAULT_BREAK_DURATION = 5L

// data class to hold the current state of our Pomodoro timer
data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.STOPPED,
    val timeLeftMillis: Long = WORK_DURATION_MILLIS,
    var brakeDuration: Long = DEFAULT_BREAK_DURATION,
    var workDuration: Long = DEFAULT_WORK_DURATION
)

enum class PomodoroPhase {
    WORK, BREAK, LONG_BREAK, PAUSED, STOPPED
}

class PomodoroService : LifecycleService() {

    private var timerJob: Job? = null

    // _pomodoroState holds the mutable state, pomodoroState exposes an immutable StateFlow
    private val _pomodoroState = MutableStateFlow(PomodoroState())
    val pomodoroState: StateFlow<PomodoroState> = _pomodoroState.asStateFlow()

    // to indicate where to go after a pause
    private var phaseBeforePause: PomodoroPhase? = null

    private var initialTimeLeft: Long = DEFAULT_WORK_DURATION
    private val _workDurationMillis = WORK_DURATION_MILLIS
    private val _breakDurationMillis = BREAK_DURATION_MILLIS

    private lateinit var settingsRepository: SettingsRepository

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_SKIP = "ACTION_SKIP"
        const val ACTION_REFRESH_SETTINGS_AND_STATE = "ACTION_REFRESH_SETTINGS_AND_STATE"


        const val NOTIFICATION_CHANNEL_ID = "pomodoro_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channel for Android O and above
        settingsRepository = (applicationContext as TaskApplication).settingsRepository
        createNotificationChannel()

//        lifecycleScope.launch {
//            settingsRepository.pomodoroBreakDuration.collect { breakDuration ->
//                Log.d("PomodoroServiceOUAAA", "Break duration updated from DataStore: $breakDuration")
//            }
//        }

        // Collect settings from DataStore using lifecycleScope
//        lifecycleScope.launch {
//            settingsRepository.pomodoroWorkDuration
//                //.map { it * 60 * 1000L } // Convert minutes to millis
//                .collect { duration ->
//                    Log.d("PomodoroService", "Work duration updated from DataStore: $duration minutes")
//                    _workDurationMillis.value = DEFAULT_WORK_DURATION * 60 * 1000L
//                    // If no timer is running and state is STOPPED, update initial timeLeft
//                    if (_pomodoroState.value.phase == PomodoroPhase.STOPPED) {
//                        _pomodoroState.value = _pomodoroState.value.copy(timeLeftMillis = duration* 60 * 1000L)
//                    }
//                }
//        }
//        lifecycleScope.launch {
//            settingsRepository.pomodoroBreakDuration
//                //.map { it * 60 * 1000L }
//                .collect {
//                    Log.d("PomodoroService", "Break duration updated from DataStore: $it minutes")
//                    _breakDurationMillis.value = DEFAULT_BREAK_DURATION * 60 * 1000L
//                }
//        }
        // To set an initial state correctly if the service is created fresh
        lifecycleScope.launch {
            _pomodoroState.value = PomodoroState(timeLeftMillis = _workDurationMillis)
        }

        lifecycleScope.launch {
            _pomodoroState.collect { state ->
                PomodoroServiceConnector.serviceStateFlow.value = state
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            ACTION_REFRESH_SETTINGS_AND_STATE -> refreshPomodoroState()
            // TODO : clean this into function
            ACTION_START -> {
                val currentWorkDurationToUse = _workDurationMillis

                _pomodoroState.value = PomodoroState( // Reset to a fresh work session
                    phase = PomodoroPhase.WORK,
                    timeLeftMillis = currentWorkDurationToUse,
                )
                startTimer(_pomodoroState.value.phase, _pomodoroState.value.timeLeftMillis)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
            ACTION_SKIP -> skipPhase()
        }
        // This ensures the service restarts if it's killed by the system
        // and re-delivers the last intent. Good for long-running services.
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("PomodoroService", "Task removed by user. Stopping timer and service.")
        // Call the same stop function that the stop button uses.
        // This will reset the state, stop the foreground service, and stop the service itself.
        stopTimer()
    }

    fun refreshPomodoroState() {

        settingsRepository = (applicationContext as TaskApplication).settingsRepository

        Log.d("PomodoroService", "ACTION_REFRESH_SETTINGS_AND_STATE received")
        // Launch a coroutine to fetch all current settings and update internal state
        lifecycleScope.launch {
            val workDuration = DEFAULT_WORK_DURATION
            val breakDuration = DEFAULT_BREAK_DURATION
            // TODO
            //val longBreakDuration = settingsRepository. * 60 * 1000L
            //val cyclesForLongBreak = settingsRepository.pomodoroCyclesBeforeLongBreak.first()

            // Update internal service StateFlows
            //_workDurationMillis = DEFAULT_WORK_DURATION
            //_breakDurationMillis = DEFAULT_BREAK_DURATION
            pomodoroState.value.brakeDuration = DEFAULT_BREAK_DURATION
            pomodoroState.value.workDuration = DEFAULT_WORK_DURATION

            // TODO
            //_longBreakDurationMillis.value = longBreakDuration
            //_cyclesBeforeLongBreak.value = cyclesForLongBreak

            // If the timer is currently stopped, update its timeLeft to the new work duration
            if (_pomodoroState.value.phase == PomodoroPhase.STOPPED) {
                _pomodoroState.value = _pomodoroState.value.copy(
                    timeLeftMillis = workDuration
                    // Potentially reset cycles too if this implies a full reset for UI display
                    // currentCycle = 0,
                    // totalCycles = 0
                )
            }
            Log.d("PomodoroService", "Settings refreshed. Work: $workDuration, Break: $breakDuration")
            Log.d("PomodoroService", "Current pomodoroState after refresh: ${_pomodoroState.value}")

        }

    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        // For a started service that communicates via StateFlow, onBind is not strictly necessary.
        // But if you wanted to bind a UI component to call methods directly, you'd use a Binder here.
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimerJob()
        Log.d("PomodoroService", "Service destroyed")
    }

    // --- Timer Control Functions ---

    private fun startTimer(phase: PomodoroPhase, duration: Long) {
        stopTimerJob() // Stop any existing timer
        _pomodoroState.value = PomodoroState(phase = phase, timeLeftMillis = duration)
        initialTimeLeft = duration
        startCountingDown()
        startForeground(NOTIFICATION_ID, buildNotification()) // Promote to foreground
        Log.d("PomodoroService", "Timer started: $phase for $duration ms")
    }

    private fun pauseTimer() {
        stopTimerJob()
        phaseBeforePause = _pomodoroState.value.phase
        _pomodoroState.value = _pomodoroState.value.copy(phase = PomodoroPhase.PAUSED)
        updateNotification() // Update notification to show paused state
        Log.d("PomodoroService", "Timer paused at ${_pomodoroState.value.timeLeftMillis} ms")
    }

    private fun resumeTimer() {
        if (_pomodoroState.value.phase == PomodoroPhase.PAUSED) {
            if (phaseBeforePause == PomodoroPhase.WORK) {
                _pomodoroState.value = _pomodoroState.value.copy(phase = PomodoroPhase.WORK)
            }
            else {
                _pomodoroState.value = _pomodoroState.value.copy(phase = PomodoroPhase.BREAK)
            }
            startTimer(_pomodoroState.value.phase, _pomodoroState.value.timeLeftMillis)
            Log.d("PomodoroService", "Timer resumed")
        }
    }

    private fun stopTimer() {
        stopTimerJob()
        _pomodoroState.value = PomodoroState() // Reset to initial state
        stopForeground(STOP_FOREGROUND_REMOVE) // Remove notification and stop foreground
        stopSelf() // Stop the service
        Log.d("PomodoroService", "Timer stopped")
    }

    private fun skipPhase() {
        val currentState = _pomodoroState.value
        val nextPhaseAndDuration = getNextPhaseAndDuration(
            currentState.phase,
        )
        // Reset current cycle count if skipping a work phase before it completes and moves to break

        startTimer(nextPhaseAndDuration.first, nextPhaseAndDuration.second)
        Log.d("PomodoroService", "Phase skipped. Next: ${nextPhaseAndDuration.first}")
    }


    private fun startCountingDown() {
        val initialTime = _pomodoroState.value.timeLeftMillis
        val currentPhase = _pomodoroState.value.phase

        timerJob = lifecycleScope.launch {
            var timeLeft = initialTime
            while (timeLeft > 0 && _pomodoroState.value.phase == currentPhase) { // Only count if phase hasn't changed
                delay(1000) // Decrement every second
                timeLeft -= 1000
                _pomodoroState.value = _pomodoroState.value.copy(timeLeftMillis = timeLeft)
                updateNotification() // Update notification with new time
            }
            if (timeLeft <= 0) {
                // Phase completed, move to next phase
                handlePhaseCompletion()
            }
        }
    }

    private fun handlePhaseCompletion() {
        val currentState = _pomodoroState.value

        val nextPhaseAndDuration = getNextPhaseAndDuration(currentState.phase)

        _pomodoroState.value = _pomodoroState.value.copy()

        startTimer(nextPhaseAndDuration.first, nextPhaseAndDuration.second)
    }

    private fun getNextPhaseAndDuration(currentPhase: PomodoroPhase): Pair<PomodoroPhase, Long> {
        return when (currentPhase) {
            PomodoroPhase.WORK -> {
                Pair(PomodoroPhase.BREAK, BREAK_DURATION_MILLIS)
            }
            PomodoroPhase.BREAK, PomodoroPhase.LONG_BREAK -> {
                Pair(PomodoroPhase.WORK, WORK_DURATION_MILLIS)
            }
            else -> Pair(PomodoroPhase.WORK, WORK_DURATION_MILLIS) // Should not happen often
        }
    }

    private fun stopTimerJob() {
        timerJob?.cancel()
        timerJob = null
    }

    // --- Notification Handling ---

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name) // Define in strings.xml
            val descriptionText = getString(R.string.channel_description) // Define in strings.xml
            val importance = NotificationManager.IMPORTANCE_LOW // Importance LOW to be less intrusive for a persistent notification
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val currentState = _pomodoroState.value
        val timeLeftFormatted = formatTime(currentState.timeLeftMillis)
        val phaseText = when (currentState.phase) {
            PomodoroPhase.WORK -> "Work Time"
            PomodoroPhase.BREAK -> "Short Break"
            PomodoroPhase.LONG_BREAK -> "Long Break"
            PomodoroPhase.PAUSED -> "Paused"
            PomodoroPhase.STOPPED -> "Stopped"
        }

        // Create an intent to open the app when the notification is tapped
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE with Android S+
        )

        // Create actions for the notification (Play/Pause, Skip, Stop)
        val playPauseActionText = if (currentState.phase == PomodoroPhase.PAUSED) "Resume" else "Pause"
        val playPauseActionIntent = Intent(this, PomodoroService::class.java).apply {
            action = if (currentState.phase == PomodoroPhase.PAUSED) ACTION_RESUME else ACTION_PAUSE
        }
        val playPausePendingIntent: PendingIntent = PendingIntent.getService(
            this,
            0,
            playPauseActionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val playPauseAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_play, // You might want custom icons here
            playPauseActionText,
            playPausePendingIntent
        ).build()

        val skipActionIntent = Intent(this, PomodoroService::class.java).apply {
            action = ACTION_SKIP
        }
        val skipPendingIntent: PendingIntent = PendingIntent.getService(
            this,
            1,
            skipActionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val skipAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_next, // You might want custom icons here
            "Skip",
            skipPendingIntent
        ).build()

        val stopActionIntent = Intent(this, PomodoroService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent: PendingIntent = PendingIntent.getService(
            this,
            2,
            stopActionIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_delete, // You might want custom icons here
            "Stop",
            stopPendingIntent
        ).build()


        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Pomodoro Timer: $phaseText")
            .setContentText("Time Left: $timeLeftFormatted")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app's icon or a custom one
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true) // Don't make sound/vibrate on every update
            .setOngoing(true) // Makes the notification non-dismissible
            .setShowWhen(false) // Don't show timestamp
            .addAction(playPauseAction)
            .addAction(skipAction)
            .addAction(stopAction)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2)) // For media style notifications with controls
            .build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification())
    }

    // Helper function to format time (e.g., 00:25:00)
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}