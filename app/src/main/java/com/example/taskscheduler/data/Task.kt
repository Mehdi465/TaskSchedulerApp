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
    private var _name: String,

    @ColumnInfo(name = "icon")
    private var _iconResId : Int,

    @ColumnInfo(name = "duration")
    private var _durationStamp : Long,

    @ColumnInfo(name = "color")
    private var _colorLong : ULong,

    @ColumnInfo(name = "priority")
    private var _priority : Priority,
    )
{
    // Inject context when needed to generate icons
    @Ignore
    var context: Context? = null

    var name: String
        get() = _name
        set(value) { _name = value }

    var iconResId: Int
        get() = _iconResId
        set(value) { _iconResId = value }

    var durationStamp: Long
        get() = _durationStamp
        set(value) { _durationStamp = value }

    var colorLong: ULong
        get() = _colorLong
        set(value) { _colorLong = value }

    var priority: Priority
        get() = _priority
        set(value) { _priority = value }


    // Exposed property: Date
    var duration: Date
        get() = Date(durationStamp)
        set(value) {
            durationStamp = value.time
        }


    // Exposed property: Icon (requires context)
    val icon: Icon?
        get() = context?.let { Icon.createWithResource(it, iconResId) }

    @Ignore
    fun setIconRes(@DrawableRes resId: Int) {
        iconResId = resId
    }

    @Ignore
    fun getName(): String{
        return name
    }

    @Ignore
    fun getColor(): ULong{
        return colorLong
    }

    @Ignore
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
            _name = "Preparation",
            _iconResId = R.drawable.book,
            _durationStamp = durationToTime(1,0),
            _colorLong = Color(0xFFFFA726).value, // Example: Orange color
            _priority = Priority.Medium // Example priority
        )

        val EXECUTION_TASK = Task(
            _name = "Execution",
            _iconResId =  R.drawable.dumbbell,
            _durationStamp = durationToTime(2,30),
            _colorLong = Color(0xFF66BB6A).value, // Example: Green color
            _priority = Priority.High // Example priority
        )

        val REVIEW_TASK = Task(
            _name = "Review",
            _iconResId =  R.drawable.language,
            _durationStamp = durationToTime(0,5),
            _colorLong = Color(0xFF42A5F5).value, // Example: Blue color
            _priority = Priority.Medium // Example priority
        )

        val COMPLETION_TASK = Task(
            _name = "Completion",
            _iconResId =  R.drawable.pen,
            _durationStamp = durationToTime(0,20),
            _colorLong = Color(0xFFAB47BC).value, // Example: Purple color
            _priority = Priority.Low // Example priority
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
