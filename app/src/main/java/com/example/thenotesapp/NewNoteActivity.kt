package com.example.thenotesapp

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.thenotesapp.databinding.ActivityEditNoteBinding
import com.example.thenotesapp.viewmodels.NoteViewModel
import java.util.*
import android.widget.ArrayAdapter

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditNoteBinding
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuration de la toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nouvelle note"

        // Initialisation du ViewModel
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        // Configuration des spinners
        setupSpinners()

        // Configuration des listeners
        setupListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return true
    }

    private fun setupSpinners() {
        // Configuration du spinner de catégorie
        val categories = resources.getStringArray(R.array.note_categories)
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.categoryAutoComplete.setAdapter(categoryAdapter)
        binding.categoryAutoComplete.setText(categories[0], false)

        // Configuration du spinner de priorité
        val priorities = arrayOf("Normale", "Importante", "Urgente")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
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

        viewModel.insertNote(
            title = title,
            content = content,
            category = category,
            priority = priority
        )

        setResult(Activity.RESULT_OK)
        finish()
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