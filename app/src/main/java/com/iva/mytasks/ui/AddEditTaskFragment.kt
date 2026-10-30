package com.iva.mytasks.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iva.mytasks.R
import com.iva.mytasks.TaskApplication
import com.iva.mytasks.data.Task
import com.iva.mytasks.databinding.FragmentAddEditTaskBinding

class AddEditTaskFragment : Fragment() {

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(
            (requireActivity().application as TaskApplication).repository
        )
    }

    // Задачата, която редактираме (null, ако добавяме нова)
    private var editingTask: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskId = arguments?.getLong(ARG_TASK_ID, NO_TASK) ?: NO_TASK
        val isEditing = taskId != NO_TASK

        // При редакция зареждаме текущото име в полето
        if (isEditing) {
            viewModel.loadTask(taskId) { task ->
                editingTask = task
                if (savedInstanceState == null) {
                    _binding?.editTitle?.setText(task?.title)
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()

            if (title.isEmpty()) {
                binding.inputLayout.error = getString(R.string.title_required)
                return@setOnClickListener
            }
            // Ако редактираме, но задачата още се зарежда - изчакваме
            if (isEditing && editingTask == null) return@setOnClickListener

            val task = editingTask
            if (task == null) {
                viewModel.addTask(title)
            } else {
                viewModel.updateTitle(task, title)
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val ARG_TASK_ID = "taskId"
        private const val NO_TASK = -1L
    }
}