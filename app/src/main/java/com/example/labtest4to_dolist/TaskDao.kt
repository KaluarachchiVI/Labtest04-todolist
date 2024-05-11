package com.example.labtest4to_dolist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

// TaskDao.kt
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): LiveData<List<Task>>


    @Query("SELECT * FROM tasks WHERE (:priority = '' OR priority = :priority)")
    fun getTasksByPriority(priority: String): LiveData<List<Task>>
}
