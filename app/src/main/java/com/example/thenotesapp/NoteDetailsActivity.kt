package com.example.thenotesapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thenotesapp.databinding.ActivityEditNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodels.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.widget.ArrayAdapter
import java.util.*

class NoteDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: NoteViewModel
    private var currentNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration de la toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Modifier la note"

        // Initialisation du ViewModel
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // Configuration des spinners
        setupSpinners()

        // Récupération de la note
        intent.getParcelableExtra<Note>("note")?.let { note ->
            currentNote = note
            loadNote(note)
        }

        // Configuration des listeners
        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return true
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
            viewModel.updateNote(updatedNote)
        }

        finish()
    }

    private fun confirmDelete() {
        currentNote?.let { note ->
            MaterialAlertDialogBuilder(this)
                .setTitle("Supprimer la note")
                .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
                .setPositiveButton("Supprimer") { _, _ ->
                    viewModel.deleteNote(note)
                    finish()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }
    }

    private fun setupSpinners() {
        // Configuration du spinner de catégorie
        val categories = resources.getStringArray(R.array.note_categories)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categoryAutoComplete.setAdapter(categoryAdapter)

        // Configuration du spinner de priorité
        val priorities = arrayOf("Normale", "Importante", "Urgente")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        binding.priorityAutoComplete.setAdapter(priorityAdapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_save -> {
                saveNote()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 