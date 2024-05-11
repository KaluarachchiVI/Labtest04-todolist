package com.example.labtest4to_dolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TaskViewModel.kt
class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository

    init {
        val taskDao = TaskDatabase.getDatabase(application.applicationContext).taskDao()
        repository = TaskRepository(taskDao)
    }

    fun getAllTasks(): LiveData<List<Task>> {
        return repository.getAllTasks()
    }

    fun insert(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(task)
        }
    }

    fun update(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(task)
        }
    }

    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return repository.getTasksByPriority(priority)
    }
}

// TaskRepository.kt
class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): LiveData<List<Task>> {
        return taskDao.getAllTasks()
    }

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    fun getTasksByPriority(priority: String): LiveData<List<Task>> {
        return taskDao.getTasksByPriority(priority)
    }
}
