package com.example.thenotesapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.thenotesapp.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM notes ORDER BY modifiedDate DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY modifiedDate DESC")
    suspend fun getAllNotesSync(): List<Note>

    @Query("SELECT * FROM notes WHERE title LIKE :query OR content LIKE :query")
    suspend fun searchNotes(query: String): List<Note>

    @Query("SELECT * FROM notes WHERE isPinned = 1 ORDER BY modifiedDate DESC")
    fun getPinnedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE isArchived = 1 ORDER BY modifiedDate DESC")
    fun getArchivedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE reminderDate IS NOT NULL ORDER BY reminderDate ASC")
    fun getNotesWithReminders(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE category = :category ORDER BY modifiedDate DESC")
    fun getNotesByCategory(category: String): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY title ASC")
    fun getNotesSortedByTitle(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY createdDate DESC")
    fun getNotesSortedByCreationDate(): LiveData<List<Note>>

    @Query("SELECT * FROM notes ORDER BY priority DESC")
    fun getNotesSortedByPriority(): LiveData<List<Note>>

    @Query("SELECT COUNT(*) FROM notes")
    fun getTotalNotesCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE isArchived = 1")
    fun getArchivedNotesCount(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE isPinned = 1")
    fun getPinnedNotesCount(): LiveData<Int>
}