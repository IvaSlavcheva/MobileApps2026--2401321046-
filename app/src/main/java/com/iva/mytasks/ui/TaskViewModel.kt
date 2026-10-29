package com.iva.mytasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.iva.mytasks.data.Task
import com.iva.mytasks.data.TaskRepository
import kotlinx.coroutines.launch

enum class TaskFilter { ALL, ACTIVE, DONE }

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val currentFilter = MutableLiveData(TaskFilter.ALL)

    val tasks: LiveData<List<Task>> = currentFilter.switchMap { filter ->
        when (filter) {
            TaskFilter.ALL -> repository.allTasks
            TaskFilter.ACTIVE -> repository.activeTasks
            TaskFilter.DONE -> repository.doneTasks
        }
    }

    fun setFilter(filter: TaskFilter) {
        currentFilter.value = filter
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            repository.insert(Task(title = title))
        }
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(isDone = !task.isDone))
        }
    }

    fun updateTitle(task: Task, newTitle: String) {
        viewModelScope.launch {
            repository.update(task.copy(title = newTitle))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}

class TaskViewModelFactory(
    private val repository: TaskRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(repository) as T
    }
}