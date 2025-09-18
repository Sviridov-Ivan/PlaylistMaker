package com.example.playlistmaker.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.adapter.TracksViewHolder
import com.example.playlistmaker.search.domain.model.Track

class TracksAdapter() : RecyclerView.Adapter<TracksViewHolder> () { // убрал private val tracks: List<Track> из конструктора (было нужно для заглушки 10 спринта)

    private val tracks = ArrayList<Track>() // var tracks = ArrayList<Track>() до создания fun updateTracks (до 11 спринта)
    private var onItemClickListener: ((Track) -> Unit)? = null // для добавления обработчика кликов на элементы треков для открытия трека, и для сохранения его в ИСТОРИЮ (спринт 12)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        // val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_track, parent, false) // традиционно inflate размещается здесь, но так необходимо по заданию
        return TracksViewHolder(parent) // здесь вместо parent было view при реализации inflate в строке выше
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        //holder.bind(tracks[position]) 11 спринт
        val track = tracks[position]
        holder.bind(track)
        holder.itemView.setOnClickListener { // для добавления обработчика кликов на элементы треков для открытия трека, и для сохранения его в ИСТОРИЮ (спринт 12)
            onItemClickListener?.invoke(track)
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) { // функция для обновления списка, создал здесь, а не в активити, ддя отсутствия дублирования списков и кор.работы Адаптера
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }

    fun clearTracks() {
        tracks.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Track) -> Unit) { // для добавления обработчика кликов на элементы треков для открытия трека, и для сохранения его в ИСТОРИЮ (спринт 12)
        onItemClickListener = listener
    }
}