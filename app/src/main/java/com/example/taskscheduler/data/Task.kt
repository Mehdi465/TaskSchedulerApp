package com.example.taskscheduler.data

import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import java.util.Date
import androidx.room.*
import com.example.taskscheduler.R
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


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
    var icon : String? = null,
    var color : Color = Color(0xFFE57373),
    )
{

    fun durationToTime(hours: Int, minutes: Int): Date {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, minutes)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }

    fun modify(task: Task){
        name = task.name
        priority = task.priority
        duration = task.duration
        icon = task.icon
        color = task.color
    }

    companion object{
        val DEFAULT_TASK = Task(name = "Default Task", priority = Priority.LOW)

        val BREAK_TASK = Task(name = "Break",
            priority = Priority.LOW,
            duration = 5.toDuration(DurationUnit.MINUTES),
            icon = "break"
        )

        object IconMap {
            val drawableMap: Map<String, Int> = mapOf(
                "pen" to R.drawable.pen,
                "book" to R.drawable.book,
                "language" to R.drawable.language,
                "runner" to R.drawable.runner,
                "dumbbell" to R.drawable.dumbbell,
                "break" to R.drawable.coffee_cups,
            )

            // if a task has no icon
            private val defaultIcon = R.drawable.runner

            fun getIconResId(iconName: String?): Int {
                return if (iconName != null) {
                    drawableMap[iconName] ?: defaultIcon
                } else {
                    defaultIcon
                }
            }
        }
    }
}
