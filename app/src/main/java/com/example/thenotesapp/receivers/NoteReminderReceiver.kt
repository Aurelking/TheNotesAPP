package com.example.thenotesapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.thenotesapp.utils.NotificationManager

class NoteReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getLongExtra("noteId", -1)
        val noteTitle = intent.getStringExtra("noteTitle") ?: "Rappel"
        val noteContent = intent.getStringExtra("noteContent") ?: ""

        if (noteId != -1L) {
            NotificationManager(context).showNoteReminder(noteId, noteTitle, noteContent)
        }
    }
} 