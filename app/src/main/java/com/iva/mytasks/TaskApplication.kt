package com.iva.mytasks

import android.app.Application
import com.iva.mytasks.data.AppDatabase
import com.iva.mytasks.data.TaskRepository

class TaskApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val repository by lazy { TaskRepository(database.taskDao()) }
}