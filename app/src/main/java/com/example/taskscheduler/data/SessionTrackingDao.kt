package com.example.taskscheduler.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface SessionTrackingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(sessionTracking: SessionTracking)

    @
}