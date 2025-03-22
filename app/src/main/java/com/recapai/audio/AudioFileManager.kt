package com.recapai.audio

import android.content.Context
import java.io.File

class AudioFileManager(private val context: Context) {

    fun getAudioStorageDir(): File {
        return File(context.getExternalFilesDir(null), "Recordings").apply {
            if (!exists()) mkdirs()
        }
    }

    fun createTempFile(): File {
        val timestamp = System.currentTimeMillis()
        return File(getAudioStorageDir(), "recording_$timestamp.mp3")
    }
}