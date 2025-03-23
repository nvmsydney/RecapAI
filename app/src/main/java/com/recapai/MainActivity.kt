package com.recapai

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recapai.audio.AudioRecorder
import com.recapai.audio.Recording
import com.recapai.audio.RecordingAdapter
import com.recapai.network.NetworkClient
import com.recapai.summarization.Summarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    // RecyclerView to display recordings
    private lateinit var recyclerView: RecyclerView
    // List to hold recordings
    private val recordings = mutableListOf<Recording>()
    // Audio recorder instance
    private lateinit var audioRecorder: AudioRecorder
    // Deepgram service for transcription
    private val deepgramService = NetworkClient.create()
    // Summarizer instance
    private lateinit var summaryGenerator: Summarizer

    // File picker launcher for selecting audio files
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val filePath = audioRecorder.handleUploadedFile(uri)
                addRecordingToList(filePath)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recordingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set adapter for RecyclerView
        recyclerView.adapter = RecordingAdapter(recordings) { recording ->
            val intent = Intent(this, SummaryActivity::class.java).apply {
                putExtra("SUMMARY", recording.fullSummary)
            }
            startActivity(intent)
        }

        // Initialize OpenAI service and summarizer
        val openAIService = NetworkClient.createOpenAIClient()
        summaryGenerator = Summarizer(openAIService)
        // Initialize audio recorder
        audioRecorder = AudioRecorder(this)

        // Set click listener for upload button
        findViewById<Button>(R.id.uploadButton).setOnClickListener {
            openFilePicker()
        }
    }

    // Open file picker to select audio files
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/mpeg"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("audio/mpeg", "audio/mp3"))
        }
        filePickerLauncher.launch(intent)
    }

    // Add recording to the list and start transcription
    private fun addRecordingToList(filePath: String) {
        val file = File(filePath)
        val recording = Recording(
            title = file.nameWithoutExtension,
            fullSummary = "Transcribing...",  // Store full summary here
            date = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Date(file.lastModified())),
            duration = getDurationFromFile(file),
            filePath = filePath
        )

        recordings.add(recording)
        recyclerView.adapter?.notifyDataSetChanged()

        // Start transcription in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val audioUrl = "https://raw.githubusercontent.com/nvmsydney/RecapAI/main/app/src/main/res/raw/interview.mp3"

                val response = deepgramService.transcribeAudio(
                    auth = "Token ${NetworkClient.DEEPGRAM_API_KEY}",
                    body = NetworkClient.createRequestBody(audioUrl)
                )

                if (response.isSuccessful) {
                    val transcript = response.body()?.results?.channels?.firstOrNull()
                        ?.alternatives?.firstOrNull()
                        ?.transcript

                    if (!transcript.isNullOrEmpty()) {
                        val summary = summaryGenerator.generateSummary(transcript)

                        withContext(Dispatchers.Main) {
                            recording.fullSummary = summary  // Update full summary
                            val position = recordings.indexOf(recording)
                            if (position != -1) {
                                recyclerView.adapter?.notifyItemChanged(position)
                            }
                            Log.d("Transcription", "Summary generated")
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            recording.fullSummary = "No transcript available"
                            recyclerView.adapter?.notifyItemChanged(recordings.indexOf(recording))
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    withContext(Dispatchers.Main) {
                        recording.fullSummary = "Transcription failed: $errorBody"
                        recyclerView.adapter?.notifyItemChanged(recordings.indexOf(recording))
                        Toast.makeText(
                            this@MainActivity,
                            "Transcription error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    recording.fullSummary = "Error: ${e.localizedMessage}"
                    val position = recordings.indexOf(recording)
                    if (position != -1) {
                        recyclerView.adapter?.notifyItemChanged(position)
                    }
                    Log.e("Transcription", "Error: ${e.message}", e)
                    Toast.makeText(
                        this@MainActivity,
                        "Processing failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Get duration of the audio file
    private fun getDurationFromFile(file: File): String {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)
            val durationMs = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0
            val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
            return String.format("%d:%02d", minutes, seconds)
        } finally {
            retriever.release()
        }
    }
}
