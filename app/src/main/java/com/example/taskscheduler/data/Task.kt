package com.example.taskscheduler.data

import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import java.util.Date
import androidx.room.*
import com.example.taskscheduler.R
import java.util.Calendar
import kotlin.time.Duration


enum class Priority {
    LOW,
    MEDIUM,
    HIGH,
    MANDATORY
}

@Entity(tableName="tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var name: String,
    var priority : Priority,
    var duration : Duration = Duration.ZERO,
    var icon : Int? = null,
    var color : Color = Color(0xFFE57373),
    )
{
    // Inject context when needed to generate icons
    @Ignore
    var context: Context? = null


    fun durationToTime(hours: Int, minutes: Int): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    companion object{

        fun durationToTime(hours: Int, minutes: Int): Long {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hours)
                set(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            return calendar.timeInMillis
        }

        val DEFAULT_TASK = Task(
            name = "Default",
            priority = Priority.LOW
        )

        val PREPARATION_TASK = Task(
            name = "Preparation",
            priority = Priority.MEDIUM
        )

        val EXECUTION_TASK = Task(
            name = "Execution",
            priority = Priority.HIGH
        )

        val REVIEW_TASK = Task(
            name = "Review",
            priority = Priority.MEDIUM
        )

        val COMPLETION_TASK = Task(
            name = "Completion",
            priority = Priority.LOW
        )

        val DEFAULT_TASKS = listOf(
            PREPARATION_TASK,
            EXECUTION_TASK,
            REVIEW_TASK,
            COMPLETION_TASK,
            EXECUTION_TASK,
            EXECUTION_TASK,
            EXECUTION_TASK,
            EXECUTION_TASK,
            EXECUTION_TASK,
            EXECUTION_TASK
        )
    }
}
