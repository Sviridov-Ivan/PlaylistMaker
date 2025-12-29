package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.entity.PlaylistEntity
import com.example.playlistmaker.media.data.converters.PlayListDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.toJsonString
import com.example.playlistmaker.util.toTrackIdList
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

    // получение плейлиста по переданному индентификатору
    override suspend fun getPlaylistById(playlistId: Long): Playlist? {
        val entity = appDatabase.playlistDao().getPlaylistById(playlistId)

        return entity?.let { playListDbConverter.map(it) }
    }

    // получение всех треков в плейлистах и фильтрация их для конкретного плейлиста
    override fun getTracksByIdsFromPlaylists(
        trackIds: List<String>
    ): Flow<List<Track>> {
        return appDatabase
            .addTrackToPlaylistDao()
            .getAllTracksFromPlayList() // получаем все треки
            .map { entities ->         //  мапим Flow (изменяем)
                entities
                    .filter { it.trackId.toString() in trackIds } // фильтруем
                    .map { playlistTrackDbConverter.map(it) }    // конвертируем в Track
            }
    }

    // удаление трека из плейлиста, из бд, с проверкой на наличие в других плейлистах и если нет, то удаление полностью из бд
    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {

        // получение плейлиста
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlistId) ?: return

        // конвертация JSON → список (из-за несоответствия) (отдельная утилита)
        val trackIdsList = playlistEntity.trackIds.toTrackIdList()

        // обновление списка trackIds
        val updatedTrackIds = trackIdsList
            .filter { it != trackId }
            .distinct() // защита от дублей в бд ПРОВЕРЬ!!
            .toMutableList() // после .distinct() чтобы была возможность изменять ПРОВЕРЬ!!!

        // обновление плейлиста
        val updatedPlaylist = playlistEntity.copy(
            trackIds = updatedTrackIds.toJsonString(), // преобразование списка назад в стринг
            trackCount = updatedTrackIds.size
        )
        appDatabase.playlistDao().updatePlaylist(updatedPlaylist)

        // получаение всех плейлистов для проверки есть ли где-то удаляемый трек
        val allPlaylists = appDatabase.playlistDao().getAllPlaylistsOnce()

        // проверка есть ли trackId хотя бы в одном плейлисте
        val isTrackUsedSomewhere = allPlaylists.any { playlist ->
            trackId in playlist.trackIds
        }

        // удаление трека из таблицы бд, если он нигде не используется
        if (!isTrackUsedSomewhere) {
            appDatabase.addTrackToPlaylistDao().deleteTrackById(trackId.toLong())
        }
    }

    // удаление плейлиста полностью с проверкой на наличие треков в других плейлистах и если нет, то удаление полностью треков из бд
    override suspend fun removePlaylist(playlist: Playlist) {

        // получение плейлиста
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(playlist.id) ?: return

        // конвертация JSON → список (из-за несоответствия) (отдельная утилита)
        val trackIdsList = playlistEntity.trackIds.toTrackIdList()

        // удаление плейлиста
        appDatabase.playlistDao().deletePlaylistById(playlist.id)

        // получаем остальные плейлисты
        val allPlaylists = appDatabase.playlistDao().getAllPlaylistsOnce()

        // для каждого trackId проверяем - используется ли он где-то еще
        trackIdsList.forEach { trackId ->
            val isTrackUsedSomewhere = allPlaylists.any { pl ->
                trackId in pl.trackIds.toTrackIdList()
            }

            // если нигде не используется трек, то удаляем трек через другой дао
            if (!isTrackUsedSomewhere) {
                appDatabase.addTrackToPlaylistDao().deleteTrackById(trackId.toLong())
            }
        }
    }

    // инициализация конвертера
    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playListDbConverter.map(playlist) }
    }
}