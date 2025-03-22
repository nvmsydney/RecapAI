package com.recapai.audio

import android.content.Context
import android.content.res.Resources
import com.recapai.R
import java.io.InputStream
import java.io.FileOutputStream

class AudioRecorder(
    private val context: Context,
    private val resources: Resources
) {
    private val fileManager = AudioFileManager(context)

    fun simulateRecording(): String {
        val outFile = fileManager.createTempFile()
        resources.openRawResource(R.raw.how_to_make_the_best_first_impressions).use { input ->
            FileOutputStream(outFile).use { output ->
                input.copyTo(output)
            }
        }
        return outFile.absolutePath
    }
}