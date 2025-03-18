package com.example.thenotesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.thenotesapp.R
import com.example.thenotesapp.databinding.FragmentEditNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodels.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var noteViewModel: NoteViewModel
    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteViewModel = ViewModelProvider(requireActivity())[NoteViewModel::class.java]

        // Récupération de la note
        arguments?.getParcelable<Note>("note")?.let { note ->
            currentNote = note
            loadNote(note)
        }

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        // Configuration du spinner de catégorie
        val categories = resources.getStringArray(R.array.note_categories)
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categoryAutoComplete.setAdapter(categoryAdapter)

        // Configuration du spinner de priorité
        val priorities = arrayOf("Normale", "Importante", "Urgente")
        val priorityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priorityAutoComplete.setAdapter(priorityAdapter)
    }

    private fun setupListeners() {
        binding.addImageButton.setOnClickListener {
            saveNote()
        }

        binding.addFileButton.setOnClickListener {
            confirmDelete()
        }
    }

    private fun loadNote(note: Note) {
        binding.apply {
            titleEditText.setText(note.title)
            contentEditText.setText(note.content)
            categoryAutoComplete.setText(note.category)
            priorityAutoComplete.setText(when (note.priority) {
                0 -> "Normale"
                1 -> "Importante"
                2 -> "Urgente"
                else -> "Normale"
            })
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

        currentNote?.let { note ->
            val updatedNote = note.copy(
                title = title,
                content = content,
                category = category,
                priority = priority,
                modifiedDate = Date()
            )
            noteViewModel.updateNote(updatedNote)
        }

        // Retour au fragment précédent
        requireActivity().onBackPressed()
    }

    private fun confirmDelete() {
        currentNote?.let { note ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    noteViewModel.deleteNote(note)
                    requireActivity().onBackPressed()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}