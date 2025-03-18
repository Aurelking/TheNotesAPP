package com.example.thenotesapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String,
    val priority: Int,
    val createdDate: Date,
    val modifiedDate: Date,
    val reminderDate: Date? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val hasAttachments: Boolean = false
) : Parcelable
