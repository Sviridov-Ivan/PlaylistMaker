package com.example.playlistmaker.media.domain.interactor

import com.example.playlistmaker.media.domain.model.NewPlaylist
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInteractor(
    private val repository: PlaylistRepository
) {

    // добавление плейлиста
    suspend fun addPlaylist(data: NewPlaylist) {
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

    // обновление плейлиста
    suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    // получение списка плейлистов
    fun getPlaylists() = repository.getPlaylists()

    // добавление трека в альбом с проверкой наличия
    suspend fun addTrackToPlaylist(
        playlist: Playlist,
        track: Track
    ) {
        repository.addTrackToPlaylist(playlist, track)
    }

    // получение плейлиста по переданному индентификатору
    suspend fun getPlaylistById(playlistId: Long) : Playlist? { // получение плейлиста по переданному индентификатору
        return repository.getPlaylistById(playlistId)
    }

    // получение отфильтрованных треков в конкретном плейлисте из бд
    fun getTracksByIdsFromPlaylists(trackIds: List<String>): Flow<List<Track>> {
        return repository.getTracksByIdsFromPlaylists(trackIds)
    }

    // получение длительности всех треков в плейлисте в минутах
    fun getTotalDurationMinutes(trackIds: List<String>): Flow<Int> {
        return repository.getTracksByIdsFromPlaylists(trackIds)
            .map { tracks ->
                // суммируем длительность всех треков
                val durationMillis = tracks.sumOf { it.trackTimeMillis }

                // преобразуем миллисекунды в строку минут
                (durationMillis / 1000 / 60).toInt()
            }
    }

    // удаление трека из плейлиста, из бд, с проверкой на наличие в других плейлистах и если нет, то удаление полностью из бд
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        repository.removeTrackFromPlaylist(playlistId,trackId)
    }

    // удаление плейлиста полностью
    suspend fun removePlaylist(playlist: Playlist) {
        repository.removePlaylist(playlist)
    }
}
