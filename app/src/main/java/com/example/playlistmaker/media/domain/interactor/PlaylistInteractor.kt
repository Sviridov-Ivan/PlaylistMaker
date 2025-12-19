package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.model.NewPlaylist
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track

class PlaylistInteractor(
    private val repository: PlaylistRepository
) {

    suspend fun addPlaylist(data: NewPlaylist) { // добавление плейлиста
        val playlist = Playlist(
            id = 0, // из Рум с автоинкрементом
            name = data.name,
            description = data.description,
            artworkPath = data.artworkPath,
            trackIds = emptyList(),
            trackCount = 0
        )
        repository.addPlaylist(playlist)
    }

    suspend fun updatePlaylist(playlist: Playlist) { // обновление плейлиста
        repository.updatePlaylist(playlist)
    }

    fun getPlaylists() = repository.getPlaylists()

    suspend fun addTrackToPlaylist( // добавление трека в альбом с проверкой наличия
        playlist: Playlist,
        track: Track
    ) {
        repository.addTrackToPlaylist(playlist, track)
    }
}