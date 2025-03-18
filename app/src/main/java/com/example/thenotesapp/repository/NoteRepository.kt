package com.example.thenotesapp.repository

import androidx.lifecycle.LiveData
import com.example.thenotesapp.database.NoteDao
import com.example.thenotesapp.model.Note

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNotes(): LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun getAllNotesSync(): List<Note> = noteDao.getAllNotesSync()

    suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun searchNotes(query: String): List<Note> = noteDao.searchNotes(query)

    fun getPinnedNotes(): LiveData<List<Note>> = noteDao.getPinnedNotes()

    fun getArchivedNotes(): LiveData<List<Note>> = noteDao.getArchivedNotes()

    fun getNotesWithReminders(): LiveData<List<Note>> = noteDao.getNotesWithReminders()
}