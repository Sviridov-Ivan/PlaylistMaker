package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist) // добавление плейлиста

    suspend fun updatePlaylist(playlist: Playlist) // обновление плейлиста

    fun getPlaylists(): Flow<List<Playlist>> // получение плейлиста

    suspend fun addTrackToPlaylist( // добавление трека в плейлист
        playlist: Playlist,
        track: Track
    )
}