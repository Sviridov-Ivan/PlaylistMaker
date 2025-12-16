package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.entity.PlaylistEntity
import com.example.playlistmaker.media.data.converters.PlayListDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playListDbConverter: PlayListDbConverter,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter
) : PlaylistRepository {

    // добавление плейлиста
    override suspend fun addPlaylist(playlist: Playlist) {
        val entity = playListDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(entity)
    }

    // обновление плейлиста
    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playListDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }

    // получение списка плейлистов
    override fun getPlaylists(): Flow<List<Playlist>> =
        appDatabase.playlistDao().getAllPlaylists().map { playlist ->
            convertFromPlaylistEntity(playlist)
    }

    // добавление трека в плейлист
    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {

        // 1. сохранение трека в таблицу треков плейлистов
        // IGNORE защитит от дубликатов
        appDatabase.addTrackToPlaylistDao().addTrack(playlistTrackDbConverter.map(track)) // использовал напрямую без инициализации конвертера

        // 2. обновление плейлиста
        val updatedPlaylist = playlist.copy(
            trackIds = playlist.trackIds + track.trackId.toString(), // приведение к одному типу стринг
            trackCount = playlist.trackCount + 1
        )
        appDatabase.playlistDao()
            .updatePlaylist(playListDbConverter.map(updatedPlaylist))
    }

    // инициализация конвертера
    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playListDbConverter.map(playlist) }
    }

}