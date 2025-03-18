package com.example.thenotesapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.thenotesapp.adapters.NoteAdapter
import com.example.thenotesapp.databinding.ActivityMainBinding
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.model.SortType
import com.example.thenotesapp.viewmodels.NoteViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity(), NoteAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NoteAdapter

    private val newNoteResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val title = data.getStringExtra("title") ?: return@let
                val content = data.getStringExtra("content") ?: return@let
                val category = data.getStringExtra("category") ?: "PERSONNEL"
                val priority = data.getIntExtra("priority", 0)

                viewModel.insertNote(title, content, category, priority)
            }
        }
    }

    private val exportResult = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        uri?.let { 
            viewModel.exportAllNotes(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupViewModel()
        setupRecyclerView()
        setupListeners()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = NoteAdapter(this)
        binding.notesRecyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
    }

    private fun setupListeners() {
        binding.fabAddNote.setOnClickListener {
            val intent = Intent(this, NewNoteActivity::class.java)
            newNoteResult.launch(intent)
        }

        binding.searchEditText.addTextChangedListener { text ->
            viewModel.searchNotes(text.toString())
        }

        binding.filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipAll -> viewModel.allNotes
                R.id.chipPinned -> viewModel.pinnedNotes
                R.id.chipArchived -> viewModel.archivedNotes
                R.id.chipWithReminders -> viewModel.notesWithReminders
                else -> viewModel.allNotes
            }.observe(this) { notes ->
                adapter.setNotes(notes)
                updateEmptyView(notes)
            }
        }
    }

    private fun observeData() {
        viewModel.allNotes.observe(this) { notes ->
            adapter.setNotes(notes)
            updateEmptyView(notes)
        }
    }

    private fun updateEmptyView(notes: List<Note>) {
        binding.emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onItemClick(note: Note) {
        openNoteDetails(note)
    }

    private fun openNoteDetails(note: Note) {
        val intent = Intent(this, NoteDetailsActivity::class.java)
        intent.putExtra("note", note)
        startActivity(intent)
    }

    private fun showNoteOptions(note: Note) {
        val options = arrayOf(
            "Modifier",
            if (note.isPinned) "Désépingler" else "Épingler",
            if (note.isArchived) "Désarchiver" else "Archiver",
            "Supprimer"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle(note.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openNoteDetails(note)
                    1 -> togglePinned(note)
                    2 -> toggleArchived(note)
                    3 -> confirmDeleteNote(note)
                }
            }
            .show()
    }

    private fun togglePinned(note: Note) {
        viewModel.updateNote(note.copy(isPinned = !note.isPinned))
    }

    private fun toggleArchived(note: Note) {
        viewModel.updateNote(note.copy(isArchived = !note.isArchived))
    }

    private fun confirmDeleteNote(note: Note) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Supprimer la note")
            .setMessage("Êtes-vous sûr de vouloir supprimer cette note ?")
            .setPositiveButton("Supprimer") { _, _ ->
                viewModel.deleteNote(note)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                showSortOptions()
                true
            }
            R.id.action_statistics -> {
                showStatistics()
                true
            }
            R.id.action_export -> {
                exportNotes()
                true
            }
            R.id.theme_light -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            R.id.theme_dark -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                true
            }
            R.id.theme_system -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortOptions() {
        val options = arrayOf(
            "Par titre",
            "Par date de création",
            "Par date de modification",
            "Par priorité",
            "Par catégorie"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("Trier les notes")
            .setItems(options) { _, which ->
                val sortType = when (which) {
                    0 -> SortType.TITLE
                    1 -> SortType.CREATION_DATE
                    2 -> SortType.MODIFICATION_DATE
                    3 -> SortType.PRIORITY
                    4 -> SortType.CATEGORY
                    else -> SortType.MODIFICATION_DATE
                }
                viewModel.setSortType(sortType)
            }
            .show()
    }

    private fun showStatistics() {
        val stats = viewModel.calculateStatistics()
        MaterialAlertDialogBuilder(this)
            .setTitle("Statistiques")
            .setMessage("""
                Nombre total de notes : ${stats.totalNotes}
                Notes épinglées : ${stats.pinnedNotes}
                Notes archivées : ${stats.archivedNotes}
                Notes avec rappel : ${stats.notesWithReminders}
                Notes avec pièces jointes : ${stats.notesWithAttachments}
                Moyenne de notes par catégorie : %.1f
            """.trimIndent().format(stats.averageNotesPerCategory))
            .setPositiveButton("OK", null)
            .show()
    }

    private fun exportNotes() {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val filename = "notes_$timestamp.txt"
        exportResult.launch(filename)
    }
}