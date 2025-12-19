package com.example.playlistmaker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0, // Идентификатор плейлиста
    val playlistName: String, // Название плейлиста
    val playlistDescription: String?, // Описание плейлиста
    val pictureUrl: String?, // Путь к файлу изображения для обложки (из хранилища уже)
    val trackIds: String, // список идентификаторов треков, преобразованный в строку (JSON)
    val trackCount: Int // количество треков
    )