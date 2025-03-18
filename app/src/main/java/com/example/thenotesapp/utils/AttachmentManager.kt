package com.example.thenotesapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AttachmentManager(private val context: Context) {
    companion object {
        private const val IMAGE_DIRECTORY = "note_images"
        private const val FILE_DIRECTORY = "note_files"
    }

    // Créer les dossiers nécessaires
    init {
        context.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        context.getDir(FILE_DIRECTORY, Context.MODE_PRIVATE)
    }

    // Sauvegarder une image depuis une Uri
    fun saveImage(imageUri: Uri): String? {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            val filename = "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
            val file = File(context.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE), filename)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Sauvegarder un fichier depuis une Uri
    fun saveFile(fileUri: Uri): String? {
        try {
            val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
            val filename = getFileNameFromUri(fileUri)
            val file = File(context.getDir(FILE_DIRECTORY, Context.MODE_PRIVATE), filename)
            
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Supprimer un fichier attaché
    fun deleteAttachment(filePath: String): Boolean {
        return try {
            File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Obtenir le nom du fichier depuis une Uri
    private fun getFileNameFromUri(uri: Uri): String {
        var result = ""
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    result = cursor.getString(displayNameIndex)
                }
            }
        }
        if (result.isEmpty()) {
            result = "file_${System.currentTimeMillis()}"
        }
        return result
    }

    // Vérifier si un fichier est une image
    fun isImageFile(filePath: String): Boolean {
        return filePath.lowercase().endsWith(".jpg") ||
               filePath.lowercase().endsWith(".jpeg") ||
               filePath.lowercase().endsWith(".png") ||
               filePath.lowercase().endsWith(".gif")
    }

    // Obtenir le type MIME d'un fichier
    fun getMimeType(filePath: String): String {
        return when {
            filePath.lowercase().endsWith(".pdf") -> "application/pdf"
            filePath.lowercase().endsWith(".doc") -> "application/msword"
            filePath.lowercase().endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            filePath.lowercase().endsWith(".jpg") || filePath.lowercase().endsWith(".jpeg") -> "image/jpeg"
            filePath.lowercase().endsWith(".png") -> "image/png"
            filePath.lowercase().endsWith(".gif") -> "image/gif"
            else -> "application/octet-stream"
        }
    }
} 