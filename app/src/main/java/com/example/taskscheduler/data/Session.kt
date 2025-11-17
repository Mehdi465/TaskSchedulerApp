package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Date

@Entity(tableName="sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @Ignore // ignore this field in database
    var scheduledTasks: List<ScheduledTask> = emptyList<ScheduledTask>(),
    var startTime: Date = Date(0L),
    var endTime: Date = Date(0L),
) {

    fun getCurrentTask(): ScheduledTask? {
        val currentTime = Calendar.getInstance().time
        return scheduledTasks.find { it.startTime.before(currentTime) && it.endTime.after(currentTime) }
    }

    fun isSessionFinished(): Boolean{
        val currentTime = Calendar.getInstance().time
        return currentTime.after(endTime)
    }
}