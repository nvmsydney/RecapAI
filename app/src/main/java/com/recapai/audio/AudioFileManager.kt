package com.recapai.audio

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class AudioFileManager(private val context: Context) {

    // Get directory for storing audio files
    fun getAudioStorageDir(): File {
        return File(context.getExternalFilesDir(null), "Recordings").apply {
            if (!exists()) mkdirs()
        }
    }

    // Copy external file to the app's storage directory
    fun copyExternalFile(sourceUri: Uri, destFileName: String): File {
        val destFile = File(getAudioStorageDir(), destFileName)
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
        return destFile
    }
}
