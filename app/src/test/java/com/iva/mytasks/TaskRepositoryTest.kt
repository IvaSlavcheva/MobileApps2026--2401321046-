package com.iva.mytasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.iva.mytasks.data.Task
import com.iva.mytasks.data.TaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: TaskRepository

    @Before
    fun setUp() {
        repository = TaskRepository(FakeTaskDao())
    }

    @Test
    fun insert_taskAppearsInAllTasks() = runTest {
        repository.insert(Task(title = "Посещение при лекар"))

        val list = repository.allTasks.value.orEmpty()

        assertEquals(1, list.size)
        assertEquals("Посещение при лекар", list[0].title)
    }

    @Test
    fun getById_existingTask_returnsIt() = runTest {
        repository.insert(Task(title = "Плащане на сметка"))
        val id = repository.allTasks.value.orEmpty()[0].id

        val task = repository.getById(id)

        assertEquals("Плащане на сметка", task?.title)
    }

    @Test
    fun getById_missingTask_returnsNull() = runTest {
        assertNull(repository.getById(999))
    }

    @Test
    fun update_markDone_movesTaskToDoneList() = runTest {
        repository.insert(Task(title = "Обаждане"))
        val task = repository.allTasks.value.orEmpty()[0]

        repository.update(task.copy(isDone = true))

        assertEquals(0, repository.activeTasks.value.orEmpty().size)
        assertEquals(1, repository.doneTasks.value.orEmpty().size)
    }

    @Test
    fun delete_removesTask() = runTest {
        repository.insert(Task(title = "За изтриване"))
        val task = repository.allTasks.value.orEmpty()[0]

        repository.delete(task)

        assertEquals(0, repository.allTasks.value.orEmpty().size)
    }
}
