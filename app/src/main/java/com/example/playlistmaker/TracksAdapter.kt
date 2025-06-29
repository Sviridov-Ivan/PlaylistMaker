package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TracksAdapter() : RecyclerView.Adapter<TracksViewHolder> () { // убрал private val tracks: List<Track> из конструктора (было нужно для заглушки 10 спринта)

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        // val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_track, parent, false) // традиционно inflate размещается здесь, но так необходимо по заданию
        return TracksViewHolder(parent) // здесь вместо parent было view при реализации inflate в строке выше
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}