package com.example.labtest4to_dolist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// TaskDatabase.kt
@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .allowMainThreadQueries() // Add this line during development only!
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQLite does not support altering column types, so we need to create a new table
                // with the desired schema, copy data, and then drop the old table

                // Create a new table with the desired schema
                database.execSQL("CREATE TABLE IF NOT EXISTS tasks_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, completed INTEGER NOT NULL, dueDate INTEGER, priority TEXT NOT NULL)")

                // Copy data from the old table to the new table
                database.execSQL("INSERT INTO tasks_new (id, name, completed, dueDate, priority) SELECT id, name, completed, dueDate, priority FROM tasks")

                // Drop the old table
                database.execSQL("DROP TABLE tasks")

                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }

    }
}