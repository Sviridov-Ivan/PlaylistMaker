package com.example.playlistmaker.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track

class PlaylistsMediaAdapter() : RecyclerView.Adapter<PlaylistsMediaViewHolder>() {

    private val playlists = ArrayList<Playlist>()

    private var onItemClickListener: ((Playlist) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsMediaViewHolder {
        return PlaylistsMediaViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PlaylistsMediaViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
//        holder.itemView.setOnClickListener { // для добавления обработчика кликов на элементы плейлистов для открытия плейлиста
//            onItemClickListener?.invoke(playlist)
//        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    fun updatePlaylists(newPlaylists: List<Playlist>) { // функция для обновления списка, создал здесь, а не в фрагменте, ддя отсутствия дублирования списков и кор.работы Адаптера
        playlists.clear()
        playlists.addAll(newPlaylists)
        notifyDataSetChanged()
    }

    fun clearPlaylists() {
        playlists.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Playlist) -> Unit) { // для добавления обработчика кликов на элементы плейлиста для открытия плейлиста
        onItemClickListener = listener
    }

}