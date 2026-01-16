package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    // добавление плейлиста (медиатека)
    suspend fun addPlaylist(playlist: Playlist)

    // обновление плейлиста (медиатека)
    suspend fun updatePlaylist(playlist: Playlist)

    // получение плейлиста (медиатека)
    fun getPlaylists(): Flow<List<Playlist>>

    // добавление трека в плейлист (плейер)
    suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    )

    // получение плейлиста по переданному индентификатору (фрагмент Плейлист)
    suspend fun getPlaylistById(playlistId: Long) : Playlist?

    // получение треков по ID в плейлисте (Плейлист)
    fun getTracksByIdsFromPlaylists(trackIds: List<String>): Flow<List<Track>>

    // удаление трека из плейлиста по Id
    suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: String
    )

    // удаление плейлиста полностью
    suspend fun removePlaylist(playlist: Playlist)

}