package com.iva.mytasks.data

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAll()
    val activeTasks: LiveData<List<Task>> = taskDao.getActive()
    val doneTasks: LiveData<List<Task>> = taskDao.getDone()

    suspend fun getById(id: Long): Task? = taskDao.getById(id)

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}