package com.recapai

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        // Get summary from intent and display it
        val summary = intent.getStringExtra("SUMMARY")
        val summaryTextView = findViewById<TextView>(R.id.summaryText)
        summaryTextView.text = summary ?: "No summary available"
    }
}
