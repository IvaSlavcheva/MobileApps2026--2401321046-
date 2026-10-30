package com.iva.mytasks.ui

import com.iva.mytasks.data.Task

object ShareFormatter {

    fun format(tasks: List<Task>, header: String): String {
        val lines = tasks.joinToString("\n") { task ->
            val mark = if (task.isDone) "[x]" else "[ ]"
            "$mark ${task.title}"
        }
        return "$header\n$lines"
    }
}