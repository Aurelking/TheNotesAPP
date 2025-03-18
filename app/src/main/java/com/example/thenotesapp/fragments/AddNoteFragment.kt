package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentAddNoteBinding
import com.example.thenotesapp.viewmodels.NoteViewModel

class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        // Configuration du spinner de catégorie
        val categories = resources.getStringArray(R.array.note_categories)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
        binding.categoryAutoComplete.setText(categories[0], false)

        // Configuration du spinner de priorité
        val priorities = arrayOf("Normale", "Importante", "Urgente")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priorityAutoComplete.setAdapter(priorityAdapter)
        binding.priorityAutoComplete.setText(priorities[0], false)
    }

    private fun setupListeners() {
        binding.addImageButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()
        val category = binding.categoryAutoComplete.text.toString()
        val priority = binding.priorityAutoComplete.text.toString().let { text ->
            when (text) {
                "Normale" -> 0
                "Importante" -> 1
                "Urgente" -> 2
                else -> 0
            }
        }

        if (title.isBlank()) {
            binding.titleEditText.error = "Le titre est requis"
            return
        }

        noteViewModel.insertNote(
            title = title,
            content = content,
            category = category,
            priority = priority
        )

        // Retour au fragment précédent
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}