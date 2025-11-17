package com.example.taskscheduler.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(tableName = "sessions_task_entry")
data class SessionTaskEntry(
    @PrimaryKey(autoGenerate = true)
    val entryId: Long = 0,
    val sessionId: Long,
    val taskId: Int,
    val startTime: Date,
    val endTime: Date
)

data class FullSessionDetails(
    @Embedded
    val session: Session, //the parent session object

    @Relation(
        parentColumn = "id", // the key in SessionEntity
        entityColumn = "sessionId"  // the matching key in SessionTaskEntry
    )
    val taskEntries: List<SessionTaskEntry> // the list of all matching children
)
