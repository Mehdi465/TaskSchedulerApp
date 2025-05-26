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
    private var name: String,

    @Embedded(prefix = "icon_")
    private var iconResId : Int,

    @ColumnInfo(name = "duration")
    private var durationStamp : Long,

    @Embedded(prefix = "color_")
    private var colorLong : ULong,

    @ColumnInfo(name = "priority")
    private var priority : Priority,
    )
{
    // Inject context when needed to generate icons
    @Ignore
    var context: Context? = null


    // Exposed property: Date
    var duration: Date
        get() = Date(durationStamp)
        set(value) {
            durationStamp = value.time
        }

    // Exposed property: Icon (requires context)
    val icon: Icon?
        get() = context?.let { Icon.createWithResource(it, iconResId) }

    fun setIconRes(@DrawableRes resId: Int) {
        iconResId = resId
    }

    fun getName(): String{
        return name
    }

    fun getColor(): ULong{
        return colorLong
    }

    fun getIconId() : Int{
        return iconResId
    }

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
            iconResId = R.drawable.book,
            durationStamp = durationToTime(1,0),
            colorLong = Color(0xFFFFA726).value, // Example: Orange color
            priority = Priority.Medium // Example priority
        )

        val EXECUTION_TASK = Task(
            name = "Execution",
            iconResId =  R.drawable.dumbbell,
            durationStamp = durationToTime(2,30),
            colorLong = Color(0xFF66BB6A).value, // Example: Green color
            priority = Priority.High // Example priority
        )

        val REVIEW_TASK = Task(
            name = "Review",
            iconResId =  R.drawable.language,
            durationStamp = durationToTime(0,5),
            colorLong = Color(0xFF42A5F5).value, // Example: Blue color
            priority = Priority.Medium // Example priority
        )

        val COMPLETION_TASK = Task(
            name = "Completion",
            iconResId =  R.drawable.pen,
            durationStamp = durationToTime(0,20),
            colorLong = Color(0xFFAB47BC).value, // Example: Purple color
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
