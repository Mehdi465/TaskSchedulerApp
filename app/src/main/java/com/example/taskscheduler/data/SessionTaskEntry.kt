package com.example.taskscheduler.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "sessions_task_entry")
data class SessionTaskEntry(
    @PrimaryKey(autoGenerate = true)
    val entryId: Long = 0,
    val sessionId: Long,
    val taskId: Int,
    val durationInMillis: Long
)

data class FullSessionDetails(
    @Embedded
    val session: Session, //the parent session object

    @Relation(
        parentColumn = "sessionId", // the key in SessionEntity
        entityColumn = "sessionId"  // the matching key in SessionTaskEntry
    )
    val taskEntries: List<SessionTaskEntry> // the list of all matching children
)
