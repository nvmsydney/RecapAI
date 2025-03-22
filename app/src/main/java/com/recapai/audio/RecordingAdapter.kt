package com.recapai.audio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.recapai.R

class RecordingAdapter(private val recordings: List<Recording>) :
    RecyclerView.Adapter<RecordingAdapter.RecordingViewHolder>() {

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.recordingTitle)
        val summary: TextView = itemView.findViewById(R.id.recordingSummary)
        val date: TextView = itemView.findViewById(R.id.recordingDate)
        val duration: TextView = itemView.findViewById(R.id.recordingDuration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recording, parent, false)
        return RecordingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        val recording = recordings[position]
        holder.title.text = recording.title
        holder.summary.text = recording.summary
        holder.date.text = recording.date
        holder.duration.text = recording.duration
    }

    override fun getItemCount() = recordings.size
}
