package com.example.thenotesapp.utils

import com.example.thenotesapp.model.Note
import java.util.*

data class NoteStatistics(
    val totalNotes: Int = 0,
    val pinnedNotes: Int = 0,
    val archivedNotes: Int = 0,
    val notesWithReminders: Int = 0,
    val notesWithAttachments: Int = 0,
    val averageNotesPerCategory: Double = 0.0
) {
    companion object {
        fun calculate(notes: List<Note>): NoteStatistics {
            if (notes.isEmpty()) return NoteStatistics()

            val categories = notes.groupBy { it.category }
            
            return NoteStatistics(
                totalNotes = notes.size,
                pinnedNotes = notes.count { it.isPinned },
                archivedNotes = notes.count { it.isArchived },
                notesWithReminders = notes.count { it.reminderDate != null },
                notesWithAttachments = notes.count { it.hasAttachments },
                averageNotesPerCategory = notes.size.toDouble() / categories.size
            )
        }
    }
} 