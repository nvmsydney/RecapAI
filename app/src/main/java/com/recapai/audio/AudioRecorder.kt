package com.recapai.audio

import android.content.Context
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AudioRecorder(
    private val context: Context
) {
    private val fileManager = AudioFileManager(context)

    // Handle uploaded file and return its path
    fun handleUploadedFile(uri: Uri): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val destFile = fileManager.copyExternalFile(uri, "upload_$timestamp.mp3")
        return destFile.absolutePath
    }
}
