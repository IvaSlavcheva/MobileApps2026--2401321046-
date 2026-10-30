package com.iva.mytasks.ui

import android.content.Intent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
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
            onClick = { task ->
                findNavController().navigate(
                    R.id.addEditTaskFragment,
                    bundleOf(AddEditTaskFragment.ARG_TASK_ID to task.id)
                )
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}