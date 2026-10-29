package com.iva.mytasks.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iva.mytasks.data.Task
import com.iva.mytasks.databinding.ItemTaskBinding

class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onDelete: (Task) -> Unit,
    private val onClick: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallback) {

    class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        with(holder.binding) {
            textTitle.text = task.title

            // Зачертаване на заглавието, ако задачата е изпълнена
            textTitle.paintFlags = if (task.isDone) {
                textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // Първо махаме стария слушател, за да не се задейства при пренарисуване
            checkDone.setOnCheckedChangeListener(null)
            checkDone.isChecked = task.isDone
            checkDone.setOnCheckedChangeListener { _, _ -> onToggle(task) }

            buttonDelete.setOnClickListener { onDelete(task) }
            root.setOnClickListener { onClick(task) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) =
            oldItem == newItem
    }
}