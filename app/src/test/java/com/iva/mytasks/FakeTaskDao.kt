package com.iva.mytasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.iva.mytasks.data.Task
import com.iva.mytasks.data.TaskDao

class FakeTaskDao : TaskDao {

    private val tasks = mutableListOf<Task>()
    private var nextId = 1L

    private val allLive = MutableLiveData<List<Task>>(emptyList())
    private val activeLive = MutableLiveData<List<Task>>(emptyList())
    private val doneLive = MutableLiveData<List<Task>>(emptyList())

    private fun refresh() {
        val sorted = tasks.sortedByDescending { it.createdAt }
        allLive.value = sorted
        activeLive.value = sorted.filter { !it.isDone }
        doneLive.value = sorted.filter { it.isDone }
    }

    override fun getAll(): LiveData<List<Task>> = allLive

    override fun getActive(): LiveData<List<Task>> = activeLive

    override fun getDone(): LiveData<List<Task>> = doneLive

    override suspend fun getById(id: Long): Task? = tasks.find { it.id == id }

    override suspend fun insert(task: Task): Long {
        val id = nextId++
        tasks.add(task.copy(id = id))
        refresh()
        return id
    }

    override suspend fun update(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index >= 0) tasks[index] = task
        refresh()
    }

    override suspend fun delete(task: Task) {
        tasks.removeAll { it.id == task.id }
        refresh()
    }
}