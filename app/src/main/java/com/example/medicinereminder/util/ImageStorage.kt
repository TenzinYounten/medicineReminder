package com.example.medicinereminder.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageStorage(private val context: Context) {
    private val imageDir = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "medicine_images"
    ).apply {
        if (!exists()) mkdirs()
    }

    suspend fun saveImage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "medicine_${UUID.randomUUID()}.jpg"
        val file = File(imageDir, fileName)

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        return file.absolutePath
    }

    fun deleteImage(path: String) {
        File(path).delete()
    }

    fun getImageUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }
}