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
        val startDate = createDate(2023, 1, 1, 10, 0)
        val endDate = createDate(2023, 1, 1, 11, 0)
        val duration = getDurationFromDates(startDate,endDate)
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


    @Test
    fun tasksToScheduledTasks_schedules_tasks_sequentially_with_correct_start_and_end_times() {
        val task1 = Task(1, "Task 1", priority = Priority.LOW, 30.minutes)
        val task2 = Task(2, "Task 2", priority = Priority.LOW, 45.minutes)
        val tasks = listOf(task1, task2)

        val basestartDateMillis = createDate(2023, 1, 1, 10, 0).time // Fixed base time

        val scheduledTasks = ScheduledTask.taskToScheduledTask(tasks, getWholeDuration(tasks))

        assertEquals("Should schedule all tasks", 2, scheduledTasks.size)

        // Verify Task 1
        assertEquals("Task 1 should be the first scheduled task", task1, scheduledTasks[0].task)
        assertEquals(
            "Task 1 start time should be the base start time",
            basestartDateMillis, scheduledTasks[0].startDate.time
        )
        assertEquals(
            "Task 1 end time should be its start time + its duration",
            basestartDateMillis + task1.duration.inWholeMilliseconds, scheduledTasks[0].endDate.time
        )

        // Verify Task 2
        assertEquals("Task 2 should be the second scheduled task", task2, scheduledTasks[1].task)
        // Task 2 should start exactly when Task 1 ends
        Assert.assertEquals(
            "Task 2 start time should be Task 1 end time",
            scheduledTasks[0].endDate.time, scheduledTasks[1].startDate.time
        )
        Assert.assertEquals(
            "Task 2 end time should be its start time + its duration",
            scheduledTasks[1].startDate.time + task2.duration.inWholeMilliseconds,
            scheduledTasks[1].endDate.time
        )
    }

    @Test
    fun tasksToScheduledTasks_calculates_correct_cumulative_start_times_from_base() {
        val task1 = Task(1, "Task 1", priority = Priority.LOW, 10.minutes)
        val task2 = Task(2, "Task 2", priority = Priority.LOW, 20.minutes)
        val task3 = Task(3, "Task 3", priority = Priority.LOW, 5.minutes)
        val tasks = listOf(task1, task2, task3)

        val basestartDateMillis = createDate(2023, 1, 1, 12, 0).time // Fixed base time

        val scheduledTasks = ScheduledTask.taskToScheduledTask(tasks, 35.minutes)

        assertEquals(3, scheduledTasks.size)

        // Task 1: Starts at basestartDateMillis, ends after 10 min
        assertEquals(basestartDateMillis, scheduledTasks[0].startDate.time)
        assertEquals(
            basestartDateMillis + task1.duration.inWholeMilliseconds,
            scheduledTasks[0].endDate.time
        )

        // Task 2: Starts when Task 1 ends, ends after 20 min
        val expectedTask2startDate = basestartDateMillis + task1.duration.inWholeMilliseconds
        assertEquals(expectedTask2startDate, scheduledTasks[1].startDate.time)
        assertEquals(
            expectedTask2startDate + task2.duration.inWholeMilliseconds,
            scheduledTasks[1].endDate.time
        )

        // Task 3: Starts when Task 2 ends, ends after 5 min
        val expectedTask3startDate = expectedTask2startDate + task2.duration.inWholeMilliseconds
        assertEquals(expectedTask3startDate, scheduledTasks[2].startDate.time)
        assertEquals(
            expectedTask3startDate + task3.duration.inWholeMilliseconds,
            scheduledTasks[2].endDate.time
        )
    }

    @Test
    fun scheduleTasksInSession_with_invalid_session_duration() {
        val task1 = Task(1, "Task 1", Priority.MANDATORY, 30.minutes,)
        val tasks = listOf(task1)
        val startDate = createDate(2023, 1, 1, 11, 0) // End before start
        val endDate = createDate(2023, 1, 1, 10, 0)
        val duration = getDurationFromDates(startDate,endDate)
        val scheduled = ScheduledTask.taskToScheduledTask(tasks, duration)
        assertTrue("Should be empty for start time after end time", scheduled.isEmpty())
    }

    @Test
    fun scheduleTasksInSession_with_zero_session_duration_returns_empty_list() {
        val task1 = Task(1, "Task 1", Priority.MANDATORY, 30.minutes,)
        val tasks = listOf(task1)
        val startDate = createDate(2023, 1, 1, 10, 0)
        val endDate = createDate(2023, 1, 1, 10, 0) // Start and end are same
        val duration = getDurationFromDates(startDate,endDate)
        val scheduled = ScheduledTask.taskToScheduledTask(tasks, duration)
        assertTrue("Should be empty for zero duration session", scheduled.isEmpty())
    }
}

