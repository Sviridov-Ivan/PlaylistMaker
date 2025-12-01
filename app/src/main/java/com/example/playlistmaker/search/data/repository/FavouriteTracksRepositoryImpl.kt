package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.converters.TrackDbConvertor
import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.FavouriteTracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
) : FavouriteTracksRepository {

    override suspend fun addToFavourites(track: Track) {
        val entity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTracks(entity)
    }

    override suspend fun removeFromFavouriteTrack(track: Track) {
        appDatabase.trackDao().deleteTrackById(track.trackId) // удалаение трека без создания entity
        //val entity = trackDbConvertor.map(track) // создание entity
        //appDatabase.trackDao().deleteTrack(entity) // удаление entity
    }

    override fun getFavouriteTracks(): Flow<List<Track>> =
        appDatabase.trackDao().getAllTracks().map { tracks ->
            convertFromTrackEntity(tracks)
        }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return appDatabase.trackDao().getFavoriteTrackIds()
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track ->trackDbConvertor.map(track) }
    }
}