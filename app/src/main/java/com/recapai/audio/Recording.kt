package com.recapai.audio

// Data class to represent a recording
data class Recording(
    val title: String,
    var fullSummary: String,
    val date: String,
    val duration: String,
    val filePath: String
) {
    // Shorten summary to get a preview for the main screen
    val summaryPreview: String
        get() = if (fullSummary.length > 100) {
            fullSummary.take(97) + "..."
        } else {
            fullSummary
        }
}
