package com.iva.mytasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.iva.mytasks.data.Task
import com.iva.mytasks.data.TaskRepository
import com.iva.mytasks.ui.TaskViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ClearDoneTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TaskViewModel
    private val observer = Observer<List<Task>> { }

    @Before
    fun setUp() {
        viewModel = TaskViewModel(TaskRepository(FakeTaskDao()))
        viewModel.tasks.observeForever(observer)
    }

    @After
    fun tearDown() {
        viewModel.tasks.removeObserver(observer)
    }

    private fun currentTasks(): List<Task> = viewModel.tasks.value.orEmpty()

    @Test
    fun clearDone_removesOnlyDoneTasks() {
        viewModel.addTask("Остава")
        viewModel.addTask("Изтрива се")
        val toFinish = currentTasks().first { it.title == "Изтрива се" }
        viewModel.toggleDone(toFinish)

        val removed = viewModel.clearDone()

        assertEquals(1, removed.size)
        assertEquals(1, currentTasks().size)
        assertEquals("Остава", currentTasks()[0].title)
    }

    @Test
    fun restoreTasks_bringsBackClearedTasks() {
        viewModel.addTask("Задача")
        viewModel.toggleDone(currentTasks()[0])
        val removed = viewModel.clearDone()
        assertTrue(currentTasks().isEmpty())

        viewModel.restoreTasks(removed)

        assertEquals(1, currentTasks().size)
    }
}
