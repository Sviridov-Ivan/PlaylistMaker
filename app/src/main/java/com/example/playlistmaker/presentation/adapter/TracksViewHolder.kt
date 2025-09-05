package com.example.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.util.dpToPx
import com.example.playlistmaker.presentation.util.formatDuration

class TracksViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.activity_track, parent, false) // по заданию, но не типичный подход, возможно для инкапсуляции логики создания View внутрь ViewHolder
) {

    private val sourceTrackImage: ImageView = itemView.findViewById(R.id.source_track_image) // переменная для работы с картинкой трека из XML
    private val sourceTrackName: TextView = itemView.findViewById(R.id.source_track_name) // переменная для работы с названием трека из XML
    private val sourceArtistName: TextView = itemView.findViewById(R.id.source_artist_name) // переменная для работы с названием артиста из XML
    private val sourceTrackTime: TextView = itemView.findViewById(R.id.source_track_time) // переменная для работы с данными тайминга трека из XML

    val radiusPx = dpToPx(
        2f,
        itemView.context
    ) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide

    fun bind(model: Track) {
        sourceTrackName.text = model.trackName
        sourceArtistName.text = model.artistName
        sourceTrackTime.text =
            formatDuration(model.trackTimeMillis) // используем отформатированное значение времени функция в файле TimeUtils
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.placeholder)
            .into(sourceTrackImage)
    }
}