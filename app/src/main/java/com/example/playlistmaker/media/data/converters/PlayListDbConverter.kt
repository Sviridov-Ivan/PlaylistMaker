package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.data.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson

class PlayListDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist) : PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.id,
            playlistName = playlist.name,
            playlistDescription = playlist.description,
            pictureUrl = playlist.artworkPath,
            trackIds = gson.toJson(playlist.trackIds), // преобразования для сохранения листа как строку в базе
            trackCount = playlist.trackCount
        )
    }

    fun map(entity: PlaylistEntity) : Playlist {
        return Playlist(
            id = entity.playlistId,
            name = entity.playlistName,
            description = entity.playlistDescription,
            artworkPath = entity.pictureUrl,
            trackIds = gson.fromJson(entity.trackIds, Array<String>::class.java).toList(), // преобразования строки из базы в список в модели
            trackCount = entity.trackCount

        )
    }
}