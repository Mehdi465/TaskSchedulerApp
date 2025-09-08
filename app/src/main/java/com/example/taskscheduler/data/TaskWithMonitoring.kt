package com.example.taskscheduler.data

import androidx.room.Embedded
import androidx.room.Relation

/*
 * This class is used to easily manipulate tasks data from database
 */

data class TaskWithTracking(
    @Embedded
    val task : Task,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val tracking : TaskTracking
)
