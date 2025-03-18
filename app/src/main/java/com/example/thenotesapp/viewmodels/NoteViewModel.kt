package com.example.thenotesapp.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.thenotesapp.database.NoteDatabase
import com.example.thenotesapp.model.Note
import com.example.thenotesapp.model.SortType
import com.example.thenotesapp.repository.NoteRepository
import com.example.thenotesapp.utils.NoteExporter
import com.example.thenotesapp.utils.NoteStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes
    val pinnedNotes: LiveData<List<Note>>
    val archivedNotes: LiveData<List<Note>>
    val notesWithReminders: LiveData<List<Note>>
    private var currentSortType: SortType = SortType.MODIFICATION_DATE

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        pinnedNotes = repository.getPinnedNotes()
        archivedNotes = repository.getArchivedNotes()
        notesWithReminders = repository.getNotesWithReminders()
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            val notes = withContext(Dispatchers.IO) {
                repository.getAllNotesSync()
            }
            _allNotes.value = sortNotes(notes)
        }
    }

    fun insertNote(
        title: String,
        content: String,
        category: String,
        priority: Int
    ) {
        val note = Note(
            title = title,
            content = content,
            category = category,
            priority = priority,
            createdDate = Date(),
            modifiedDate = Date()
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertNote(note)
            loadNotes()
        }
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNote(note)
        loadNotes()
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNote(note)
        loadNotes()
    }

    fun searchNotes(query: String?) = viewModelScope.launch {
        val searchQuery = query?.takeIf { it.isNotBlank() }?.let { "%$it%" } ?: "%"
        val notes = withContext(Dispatchers.IO) {
            repository.searchNotes(searchQuery)
        }
        _allNotes.value = sortNotes(notes)
    }

    fun setSortType(sortType: SortType) {
        currentSortType = sortType
        _allNotes.value?.let {
            _allNotes.value = sortNotes(it)
        }
    }

    private fun sortNotes(notes: List<Note>): List<Note> {
        return when (currentSortType) {
            SortType.TITLE -> notes.sortedBy { it.title }
            SortType.CREATION_DATE -> notes.sortedByDescending { it.createdDate }
            SortType.MODIFICATION_DATE -> notes.sortedByDescending { it.modifiedDate }
            SortType.PRIORITY -> notes.sortedByDescending { it.priority }
            SortType.CATEGORY -> notes.sortedBy { it.category }
            SortType.NONE -> notes
        }
    }

    fun exportAllNotes(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        try {
            val notes = repository.getAllNotesSync()
            getApplication<Application>().contentResolver.openOutputStream(uri)?.use { outputStream ->
                NoteExporter.exportToFile(notes, outputStream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun calculateStatistics(): NoteStatistics {
        val notes = _allNotes.value ?: emptyList()
        return NoteStatistics.calculate(notes)
    }
} 