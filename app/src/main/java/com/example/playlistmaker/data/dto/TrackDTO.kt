package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.model.Track
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TrackDTO(
    val trackId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека миллисекундах, так приходит от база iTunes Search API.
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String?, // Ссылка на бесплатный отрывок
    val collectionName: String?, // название альбома (может отсутствовать)
    val releaseDate: String?, // дата релиза (опционально)
    val primaryGenreName: String?, // жанр
    val country: String? // страна
) : Parcelable // для возможности передачи данных не только в RecyclerView, но и в class AudioPlayerActivity

