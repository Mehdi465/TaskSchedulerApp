package com.example.taskscheduler.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskscheduler.data.Converters.ColorConverters
import com.example.taskscheduler.data.Converters.DateConverters
import com.example.taskscheduler.data.Converters.DurationConverters

@Database(
    entities = [Task::class,TaskTracking::class, SessionTracking::class],
    version = 5,
    exportSchema = true
)
@TypeConverters(DurationConverters::class, ColorConverters::class, DateConverters::class)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
    abstract fun taskTrackingDao(): TaskTrackingDao

    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        // Migration from 1 -> 2, added duration column to Task
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQL command to add the new 'duration' column to the 'tasks' table
                // We also specify a DEFAULT value for existing rows.
                database.execSQL("ALTER TABLE tasks ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Migration from 2 -> 3, added Color and Icon columns to Task
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                val defaultColorArgb =
                    Color.White.toArgb()
                database.execSQL("ALTER TABLE tasks ADD COLUMN Color INTEGER NOT NULL DEFAULT $defaultColorArgb")
                database.execSQL("ALTER TABLE tasks ADD COLUMN icon INTEGER DEFAULT NULL")
            }
        }

        fun getDatabase(context: Context): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                    //.addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}