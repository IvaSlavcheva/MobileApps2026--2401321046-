package com.iva.mytasks

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.iva.mytasks.data.AppDatabase
import com.iva.mytasks.data.TaskRepository

class TaskApplication : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val repository by lazy { TaskRepository(database.taskDao()) }

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PREFS, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        )
    }

    companion object {
        const val PREFS = "settings"
        const val KEY_NIGHT_MODE = "night_mode"
    }
}