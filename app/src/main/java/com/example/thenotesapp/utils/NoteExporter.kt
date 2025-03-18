package com.example.thenotesapp.utils

import android.content.Context
import android.net.Uri
import com.example.thenotesapp.model.Note
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class NoteExporter(private val context: Context) {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // Exporter une note au format texte
    fun exportToTxt(note: Note, outputUri: Uri) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            val content = buildString {
                appendLine("Titre: ${note.title}")
                appendLine("Date de création: ${dateFormat.format(note.createdDate)}")
                appendLine("Dernière modification: ${dateFormat.format(note.modifiedDate)}")
                appendLine("Catégorie: ${note.category}")
                appendLine("Priorité: ${getPriorityText(note.priority)}")
                if (note.isPinned) appendLine("Note épinglée")
                if (note.isArchived) appendLine("Note archivée")
                note.reminderDate?.let {
                    appendLine("Rappel prévu le: ${dateFormat.format(it)}")
                }
                appendLine("\nContenu:")
                appendLine(note.content)
            }
            outputStream.write(content.toByteArray())
        }
    }

    // Exporter une liste de notes au format texte
    fun exportNotesToTxt(notes: List<Note>, outputUri: Uri) {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream ->
            val content = buildString {
                appendLine("Export de notes - ${dateFormat.format(Date())}")
                appendLine("Nombre total de notes: ${notes.size}")
                appendLine("=" .repeat(50))
                
                notes.forEach { note ->
                    appendLine("\nNote #${note.id}")
                    appendLine("-".repeat(20))
                    appendLine("Titre: ${note.title}")
                    appendLine("Date de création: ${dateFormat.format(note.createdDate)}")
                    appendLine("Catégorie: ${note.category}")
                    appendLine("Priorité: ${getPriorityText(note.priority)}")
                    if (note.isPinned) appendLine("Note épinglée")
                    if (note.isArchived) appendLine("Note archivée")
                    note.reminderDate?.let {
                        appendLine("Rappel prévu le: ${dateFormat.format(it)}")
                    }
                    appendLine("\nContenu:")
                    appendLine(note.content)
                    appendLine("=" .repeat(50))
                }
            }
            outputStream.write(content.toByteArray())
        }
    }

    // Obtenir le texte de la priorité
    private fun getPriorityText(priority: Int): String {
        return when (priority) {
            0 -> "Normale"
            1 -> "Importante"
            2 -> "Urgente"
            else -> "Non définie"
        }
    }

    // Créer un fichier temporaire pour l'export
    fun createTempFile(prefix: String = "note", suffix: String = ".txt"): File {
        return File.createTempFile(prefix, suffix, context.cacheDir)
    }

    companion object {
        fun exportToFile(notes: List<Note>, outputStream: OutputStream) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val content = buildString {
                appendLine("Notes exportées le ${dateFormat.format(Date())}")
                appendLine("Nombre total de notes : ${notes.size}")
                appendLine("----------------------------------------")
                appendLine()

                notes.forEach { note ->
                    appendLine("Titre : ${note.title}")
                    appendLine("Contenu : ${note.content}")
                    appendLine("Catégorie : ${note.category}")
                    appendLine("Priorité : ${getPriorityText(note.priority)}")
                    appendLine("Créée le : ${dateFormat.format(note.createdDate)}")
                    appendLine("Modifiée le : ${dateFormat.format(note.modifiedDate)}")
                    if (note.isPinned) appendLine("Note épinglée")
                    if (note.isArchived) appendLine("Note archivée")
                    note.reminderDate?.let {
                        appendLine("Rappel prévu le : ${dateFormat.format(it)}")
                    }
                    appendLine("----------------------------------------")
                    appendLine()
                }
            }

            outputStream.write(content.toByteArray())
            outputStream.flush()
        }

        private fun getPriorityText(priority: Int): String = when (priority) {
            0 -> "Normale"
            1 -> "Importante"
            2 -> "Urgente"
            else -> "Normale"
        }
    }
} 