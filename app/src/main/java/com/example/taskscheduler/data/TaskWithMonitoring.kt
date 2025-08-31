package com.example.taskscheduler.data

import androidx.room.Embedded
import androidx.room.Relation

/*
 * This class is used to easily manipulate tasks datas from database
 */

data class TaskWithMonitoring(
    @Embedded
    val task : Task,

    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val monitoring : TaskMonitoring
)
