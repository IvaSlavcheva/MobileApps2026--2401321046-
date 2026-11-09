package com.iva.mytasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.iva.mytasks.data.Task
import com.iva.mytasks.data.TaskRepository
import com.iva.mytasks.ui.TaskFilter
import com.iva.mytasks.ui.TaskViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TaskViewModel
    private val observer = Observer<List<Task>> { }

    @Before
    fun setUp() {
        viewModel = TaskViewModel(TaskRepository(FakeTaskDao()))
        // Без наблюдател LiveData няма стойност
        viewModel.tasks.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModel.tasks.removeObserver(observer)
    }

    private fun currentTasks(): List<Task> = viewModel.tasks.value.orEmpty()

    @Test
    fun addTask_appearsInList() {
        viewModel.addTask("Посещение при лекар")

        assertEquals(1, currentTasks().size)
        assertEquals("Посещение при лекар", currentTasks()[0].title)
        assertFalse(currentTasks()[0].isDone)
    }

    @Test
    fun toggleDone_changesStatus() {
        viewModel.addTask("Плащане на сметка")

        viewModel.toggleDone(currentTasks()[0])
        assertTrue(currentTasks()[0].isDone)

        viewModel.toggleDone(currentTasks()[0])
        assertFalse(currentTasks()[0].isDone)
    }

    @Test
    fun updateTitle_changesOnlyTitle() {
        viewModel.addTask("Старо име")

        viewModel.updateTitle(currentTasks()[0], "Ново име")

        assertEquals(1, currentTasks().size)
        assertEquals("Ново име", currentTasks()[0].title)
    }

    @Test
    fun deleteTask_removesItFromList() {
        viewModel.addTask("За изтриване")

        viewModel.deleteTask(currentTasks()[0])

        assertTrue(currentTasks().isEmpty())
    }

    @Test
    fun setFilter_showsOnlyMatchingTasks() {
        viewModel.addTask("Активна")
        viewModel.addTask("Изпълнена")
        val done = currentTasks().first { it.title == "Изпълнена" }
        viewModel.toggleDone(done)

        viewModel.setFilter(TaskFilter.ACTIVE)
        assertEquals(listOf("Активна"), currentTasks().map { it.title })

        viewModel.setFilter(TaskFilter.DONE)
        assertEquals(listOf("Изпълнена"), currentTasks().map { it.title })

        viewModel.setFilter(TaskFilter.ALL)
        assertEquals(2, currentTasks().size)
    }

    @Test
    fun loadTask_existingTask_returnsIt() {
        viewModel.addTask("Зареди ме")
        val id = currentTasks()[0].id
        var loaded: Task? = null

        viewModel.loadTask(id) { loaded = it }

        assertEquals("Зареди ме", loaded?.title)
    }

    @Test
    fun loadTask_missingTask_returnsNull() {
        var loaded: Task? = Task(title = "не е null")

        viewModel.loadTask(999) { loaded = it }

        assertNull(loaded)
    }
}
