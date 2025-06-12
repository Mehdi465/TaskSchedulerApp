package com.example.taskscheduler.data

import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import java.util.Date
import androidx.room.*
import com.example.taskscheduler.R
import java.util.Calendar


enum class Priority {
    Low,
    Medium,
    High,
    Mandatory
}

@Entity(tableName="tasks")
class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    /*
    @ColumnInfo(name = "icon")
    var iconResId : Int,

    @ColumnInfo(name = "duration")
    var durationStamp : Long,

    @ColumnInfo(name = "color")
    var colorLong : ULong,
    */

    @ColumnInfo(name = "priority")
    var priority : Priority,
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

        val PREPARATION_TASK = Task(
            name = "Preparation",
            priority = Priority.Medium // Example priority
        )

        val EXECUTION_TASK = Task(
            name = "Execution",
            priority = Priority.High // Example priority
        )

        val REVIEW_TASK = Task(
            name = "Review",
            priority = Priority.Medium // Example priority
        )

        val COMPLETION_TASK = Task(
            name = "Completion",
            priority = Priority.Low // Example priority
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
