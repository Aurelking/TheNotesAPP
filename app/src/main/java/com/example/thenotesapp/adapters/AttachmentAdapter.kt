package com.example.thenotesapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.thenotesapp.R
import com.example.thenotesapp.utils.AttachmentManager
import java.io.File

class AttachmentAdapter(
    private val attachmentManager: AttachmentManager,
    private val onAttachmentClick: (String) -> Unit,
    private val onAttachmentDelete: (String) -> Unit
) : RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder>() {

    private val attachments = mutableListOf<String>()

    fun setAttachments(imagePath: String?, filePath: String?) {
        attachments.clear()
        imagePath?.let { attachments.add(it) }
        filePath?.let { attachments.add(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttachmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment, parent, false)
        return AttachmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(attachments[position])
    }

    override fun getItemCount(): Int = attachments.size

    inner class AttachmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconView: ImageView = itemView.findViewById(R.id.attachmentIcon)
        private val nameView: TextView = itemView.findViewById(R.id.attachmentName)
        private val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        init {
            itemView.setOnClickListener {
                onAttachmentClick(attachments[adapterPosition])
            }
            deleteButton.setOnClickListener {
                onAttachmentDelete(attachments[adapterPosition])
            }
        }

        fun bind(path: String) {
            val file = File(path)
            nameView.text = file.name

            // Définir l'icône en fonction du type de fichier
            iconView.setImageResource(
                if (attachmentManager.isImageFile(path)) {
                    R.drawable.ic_image
                } else {
                    R.drawable.ic_file
                }
            )
        }
    }
} 