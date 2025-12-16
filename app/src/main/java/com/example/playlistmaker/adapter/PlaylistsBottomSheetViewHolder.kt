package com.example.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.playlistmaker.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.util.dpToPx

class PlaylistsBottomSheetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_playlist_bottom_sheet,  parent, false)
) {

    private val sourceArtworkPath: ImageView = itemView.findViewById(R.id.playlist_image_item_bottom_sheet)

    private val sourcePlaylistName: TextView = itemView.findViewById(R.id.playlist_name_item_bottom_sheet)

    private val sourceTrackCount: TextView = itemView.findViewById(R.id.playlist_track_count_item_bottom_sheet)

    val radiusPx = dpToPx(
        2f,
        itemView.context
    ) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide

    fun bind(model: Playlist) {
        sourcePlaylistName.text = model.name
        sourceTrackCount.text = itemView.context.getString(R.string.playlist_tracks_count_format, model.trackCount)

        Glide.with(itemView)
            .load(model.artworkPath)
            .centerCrop()
            .transform(RoundedCorners(radiusPx))
            .placeholder(R.drawable.placeholder)
            .into(sourceArtworkPath)
    }
}