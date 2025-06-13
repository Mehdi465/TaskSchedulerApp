package com.example.taskscheduler.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskscheduler.data.Converters.DurationConverters

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(DurationConverters::class)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance: TaskDatabase? = null

        // Migration from 1 -> 2, added duration column to Task
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQL command to add the new 'duration' column to the 'tasks' table
                // We also specify a DEFAULT value for existing rows.
                database.execSQL("ALTER TABLE tasks ADD COLUMN duration INTEGER NOT NULL DEFAULT 0")
                // Note: SQLite stores Long as INTEGER. NOT NULL with DEFAULT 0 is good practice.
            }
        }

        fun getDatabase(context: Context): TaskDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "task_database")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}