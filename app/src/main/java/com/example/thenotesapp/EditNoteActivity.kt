package com.example.thenotesapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thenotesapp.databinding.ActivityEditNoteBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.viewmodels.NoteViewModel
import java.util.*
import android.widget.ArrayAdapter

class EditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: NoteViewModel
    private var currentNote: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupViewModel()
        setupInputFields()
        setupListeners()

        currentNote = intent.getParcelableExtra("note")
        currentNote?.let { note ->
            loadNote(note)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
    }

    private fun setupInputFields() {
        // Configuration des catégories
        val categories = arrayOf("PERSONNEL", "TRAVAIL", "URGENT", "IDÉES", "COURSES")
        val categoriesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categoryAutoComplete.setAdapter(categoriesAdapter)

        // Configuration des priorités
        val priorities = arrayOf("Normale", "Importante", "Urgente")
        val prioritiesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        binding.priorityAutoComplete.setAdapter(prioritiesAdapter)
    }

    private fun setupListeners() {
        binding.fabSave.setOnClickListener {
            saveNote()
        }
    }

    private fun loadNote(note: Note) {
        binding.apply {
            titleEditText.setText(note.title)
            contentEditText.setText(note.content)
            categoryAutoComplete.setText(note.category)
            priorityAutoComplete.setText(getPriorityText(note.priority))
        }

        supportActionBar?.title = "Modifier la note"
    }

    private fun saveNote() {
        val title = binding.titleEditText.text.toString()
        val content = binding.contentEditText.text.toString()
        val category = binding.categoryAutoComplete.text.toString()
        val priority = getPriorityValue(binding.priorityAutoComplete.text.toString())

        if (title.isBlank()) {
            binding.titleLayout.error = "Le titre ne peut pas être vide"
            return
        }

        if (currentNote == null) {
            viewModel.insertNote(title, content, category, priority)
        } else {
            val updatedNote = currentNote!!.copy(
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

    private fun getPriorityText(priority: Int): String = when (priority) {
        0 -> "Normale"
        1 -> "Importante"
        2 -> "Urgente"
        else -> "Normale"
    }

    private fun getPriorityValue(priorityText: String): Int = when (priorityText) {
        "Importante" -> 1
        "Urgente" -> 2
        else -> 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 