package com.example.ScheduledTask.data

import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.isEmpty
import com.example.taskscheduler.data.Priority
import com.example.taskscheduler.data.ScheduledTask
import com.example.taskscheduler.data.ScheduledTask.Companion.getDurationFromDates
import com.example.taskscheduler.data.Task
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
// Import Google Truth if you added it, for more readable assertions
// import com.google.common.truth.Truth.assertThat

import java.util.Calendar
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ScheduledTaskTest {

    // Helper to create Date objects easily for testing
    private fun createDate(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int = 0
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day, hour, minute, second) // Month is 0-indexed
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getWholeDuration(tasks: List<Task>): Duration {
        var duration = Duration.ZERO
        for (task in tasks) {
            duration += task.duration
        }
        return duration
    }


    // --- Tests for tasksToScheduledTasks (within ScheduledTask.Companion) ---

    @Test
    fun tasksToScheduledTasks_with_empty_list() {
        val tasks = emptyList<Task>()
        // Pass a fixed base start time for predictability
        val scheduledTasks = ScheduledTask.taskToScheduledTask(tasks, getWholeDuration(tasks))
        assertTrue("Scheduled tasks should be empty for empty input", scheduledTasks.isEmpty())
        // Truth: assertThat(scheduledTasks).isEmpty()
    }

    @Test
    fun scheduleTasksInSession_with_no_tasks_returns_empty_list0() {
        val tasks = emptyList<Task>()
        val startTime = createDate(2023, 1, 1, 10, 0)
        val endTime = createDate(2023, 1, 1, 11, 0)
        val duration = getDurationFromDates(startTime,endTime)
        val scheduled = ScheduledTask.taskToScheduledTask(tasks, duration)
        assertTrue(scheduled.isEmpty())
    }

    @Test
    fun checkMandatoryScheduling(){
        val task_m1 = Task(1, "Task 1", priority = Priority.MANDATORY, 30.minutes)
        val task_l2 = Task(1, "Task 2", priority = Priority.LOW, 15.minutes)
        val task_h3 = Task(1, "Task 3", priority = Priority.HIGH, 25.minutes)

        val not_mandatory_tasks_duration = getWholeDuration(listOf(task_l2,task_h3))
        val tasks = listOf(task_m1,task_l2,task_h3)
        val scheduledTasks = ScheduledTask.taskToScheduledTask(tasks, getWholeDuration(tasks)-not_mandatory_tasks_duration)

        assertEquals("","Task 1",scheduledTasks[0].task.name)
    }
}

