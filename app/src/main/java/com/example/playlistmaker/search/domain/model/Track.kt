package com.example.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize // для возможности передачи данных не только в RecyclerView, но и в class AudioPlayerActivity
data class Track(
    val trackId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека миллисекундах, так приходит от база iTunes Search API.
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String?, // Ссылка на бесплатный отрывок
    val collectionName: String?, // название альбома (может отсутствовать)
    val releaseDate: String?, // дата релиза (опционально)
    val primaryGenreName: String?, // жанр
    val country: String?, // страна
    var isFavorite: Boolean = false // определять, добавлен ли данный трек в избранное
) : Parcelable // для возможности передачи данных не только в RecyclerView, но и в class AudioPlayerActivity