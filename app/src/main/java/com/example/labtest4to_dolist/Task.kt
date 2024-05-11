package com.example.labtest4to_dolist

import androidx.room.Entity
import androidx.room.PrimaryKey

// Task.kt
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name: String,
    val completed: Boolean = false,
    val dueDate: Long? = null,
    val priority: String  // Low for low priority, Normal for medium priority, High for high priority
)
