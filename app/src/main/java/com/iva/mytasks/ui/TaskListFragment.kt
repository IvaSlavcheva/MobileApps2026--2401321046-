package com.iva.mytasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iva.mytasks.R
import com.iva.mytasks.TaskApplication
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
            onDelete = { task -> viewModel.deleteTask(task) },
            onClick = { }
        )
        binding.recyclerTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTasks.adapter = adapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.submitList(tasks)
            binding.textEmpty.visibility =
                if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when (checkedIds.firstOrNull()) {
                R.id.chipActive -> TaskFilter.ACTIVE
                R.id.chipDone -> TaskFilter.DONE
                else -> TaskFilter.ALL
            }
            viewModel.setFilter(filter)
        }

        // ВРЕМЕННО: за да виждаме задачи, докато нямаме екран за добавяне
        binding.fabAdd.setOnClickListener {
            viewModel.addTask("Тестова задача")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}