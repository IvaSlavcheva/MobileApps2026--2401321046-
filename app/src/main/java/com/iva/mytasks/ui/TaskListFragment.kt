package com.iva.mytasks.ui

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.iva.mytasks.R
import com.iva.mytasks.TaskApplication
import com.iva.mytasks.data.Task
import com.iva.mytasks.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as TaskApplication).repository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskAdapter(
            onToggle = { task -> viewModel.toggleDone(task) },
            onDelete = { task -> deleteWithUndo(task) },
            onClick = { task ->
                findNavController().navigate(
                    R.id.addEditTaskFragment,
                    bundleOf(AddEditTaskFragment.ARG_TASK_ID to task.id)
                )
            }
        )
        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter
        attachSwipeToDelete(adapter)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            binding.textEmpty.visibility =
                if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.progress.observe(viewLifecycleOwner) { (done, total) ->
            binding.textProgress.text = if (total == 0) {
                getString(R.string.progress_empty)
            } else {
                getString(R.string.progress_format, done, total)
            }
            binding.progressBar.max = maxOf(total, 1)
            binding.progressBar.setProgressCompat(done, true)
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipActive -> TaskFilter.ACTIVE
                R.id.chipDone -> TaskFilter.DONE
                else -> TaskFilter.ALL
            }
            viewModel.setFilter(filter)
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.addEditTaskFragment)
        }

        binding.buttonShare.setOnClickListener {
            val tasks = viewModel.tasks.value.orEmpty()
            if (tasks.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    R.string.nothing_to_share,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val text = ShareFormatter.format(tasks, getString(R.string.share_header))
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_list)))
        }
    }

    /** Изтрива задачата и показва лента с бутон "Отмени". */
    private fun deleteWithUndo(task: Task) {
        viewModel.deleteTask(task)
        Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { viewModel.restoreTask(task) }
            .setAnchorView(binding.fabAdd)
            .show()
    }

    /** Плъзгане наляво или надясно изтрива задачата. */
    private fun attachSwipeToDelete(adapter: TaskAdapter) {
        val radius = resources.displayMetrics.density * 20
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = MaterialColors.getColor(
                binding.root,
                com.google.android.material.R.attr.colorErrorContainer
            )
        }

        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    deleteWithUndo(adapter.currentList[position])
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (dX != 0f) {
                    val v = viewHolder.itemView
                    val rect = RectF(
                        v.left.toFloat(),
                        v.top.toFloat(),
                        v.right.toFloat(),
                        v.bottom.toFloat()
                    )
                    c.drawRoundRect(rect, radius, radius, paint)
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerTasks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}