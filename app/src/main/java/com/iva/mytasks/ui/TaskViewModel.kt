package com.iva.mytasks.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
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

    /** Двойка (изпълнени, всички) - не зависи от избрания филтър. */
    val progress: LiveData<Pair<Int, Int>> = repository.allTasks.map { list ->
        list.count { it.isDone } to list.size
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

    /** Връща изтрита задача (за бутона "Отмени"). */
    fun restoreTask(task: Task) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    /** Изтрива всички изпълнени задачи и връща списъка с тях (за "Отмени"). */
    fun clearDone(): List<Task> {
        val done = repository.allTasks.value.orEmpty().filter { it.isDone }
        viewModelScope.launch {
            done.forEach { repository.delete(it) }
        }
        return done
    }

    /** Връща няколко изтрити задачи наведнъж. */
    fun restoreTasks(tasks: List<Task>) {
        viewModelScope.launch {
            tasks.forEach { repository.insert(it) }
        }
    }

    fun loadTask(id: Long, onLoaded: (Task?) -> Unit) {
        viewModelScope.launch {
            onLoaded(repository.getById(id))
        }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(repository) as T
}
