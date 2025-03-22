package com.recapai

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recapai.audio.AudioRecorder
import com.recapai.audio.Recording
import com.recapai.audio.RecordingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val recordings = mutableListOf<Recording>()
    private lateinit var audioRecorder: AudioRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize audio components
        audioRecorder = AudioRecorder(this, resources)

        // Setup UI components
        val startBtn = findViewById<Button>(R.id.startRecordingBtn)
        val stopBtn = findViewById<Button>(R.id.stopRecordingBtn)

        startBtn.setOnClickListener {
            val filePath = audioRecorder.simulateRecording()
            addRecordingToList(filePath)
            startBtn.isEnabled = false
            stopBtn.isEnabled = true
            Toast.makeText(this, "Recording simulated!", Toast.LENGTH_SHORT).show()
        }

        stopBtn.setOnClickListener {
            startBtn.isEnabled = true
            stopBtn.isEnabled = false
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show()
        }

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recordingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecordingAdapter(recordings)
    }

    private fun addRecordingToList(filePath: String) {
        // TODO: pull actual data from the mp3 files to populate the recording items
        // TODO: translate audio to text, pass into LLM and display in the UI
        recordings.add(
            Recording(
                title = generateRecordingTitle(),
                summary = "Automated meeting summary",
                date = SimpleDateFormat("MM/dd/yy", Locale.getDefault()).format(Date()),
                duration = "2:45",
                filePath = filePath
            )
        )
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun generateRecordingTitle(): String {
        return "Meeting ${recordings.size + 1}"
    }
}