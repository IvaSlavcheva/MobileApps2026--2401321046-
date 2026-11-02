package com.iva.mytasks

import com.iva.mytasks.data.Task
import com.iva.mytasks.ui.ShareFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareFormatterTest {

    @Test
    fun format_marksActiveAndDoneTasks() {
        val tasks = listOf(
            Task(id = 1, title = "Посещение при лекар"),
            Task(id = 2, title = "Плащане на сметка", isDone = true)
        )

        val result = ShareFormatter.format(tasks, "Моите задачи:")

        val expected = "Моите задачи:\n" +
                "[ ] Посещение при лекар\n" +
                "[x] Плащане на сметка"
        assertEquals(expected, result)
    }

    @Test
    fun format_singleActiveTask_hasEmptyCheckbox() {
        val tasks = listOf(Task(id = 1, title = "Обаждане на лекар"))

        val result = ShareFormatter.format(tasks, "Задачи:")

        assertEquals("Задачи:\n[ ] Обаждане на лекар", result)
    }
}