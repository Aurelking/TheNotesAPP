package com.example.thenotesapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.thenotesapp.databinding.ItemNoteBinding
import com.example.thenotesapp.model.Note
import android.view.View
import com.example.thenotesapp.R
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    private var notes = emptyList<Note>()

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(notes[position])
                }
            }
        }

        fun bind(note: Note) {
            binding.apply {
                noteTitle.text = note.title
                noteContent.text = note.content
                noteCategory.text = note.category
                modifiedDate.text = formatDate(note.modifiedDate)
                
                // Gestion des indicateurs
                pinIcon.visibility = if (note.isPinned) View.VISIBLE else View.GONE
                reminderIndicator.visibility = if (note.reminderDate != null) View.VISIBLE else View.GONE
                attachmentIndicator.visibility = if (note.hasAttachments) View.VISIBLE else View.GONE

                // Gestion de la priorité
                val priorityColor = when (note.priority) {
                    0 -> R.color.priority_normal
                    1 -> R.color.priority_important
                    2 -> R.color.priority_urgent
                    else -> R.color.priority_normal
                }
                priorityIndicator.setBackgroundResource(priorityColor)
            }
        }

        private fun formatDate(date: Date): String {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return "Modifié le ${formatter.format(date)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount() = notes.size

    fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(note: Note)
    }
} 