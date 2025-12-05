package com.example.playlistmaker.search.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table") // kласс TrackEntity является сущностью таблицы movie_table TrackEntity
data class TrackEntity(
    @PrimaryKey // поле id Long тип и является первичным ключом таблицы (аннотация PrimaryKey)
    val trackId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека миллисекундах, так приходит от база iTunes Search API.
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String?, // Ссылка на бесплатный отрывок)
    val collectionName: String?, // название альбома (может отсутствовать)
    val releaseDate: String?, // дата релиза (опционально)
    val primaryGenreName: String?, // жанр
    val country: String?, // страна
    val isFavorite: Boolean, // отметка "избранный трек"
    val addedAt: Long = System.currentTimeMillis() // поле для времени добавления
)
