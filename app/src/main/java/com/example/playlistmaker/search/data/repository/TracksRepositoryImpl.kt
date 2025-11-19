package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.data.dto.TrackDTO
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TracksRepositoryImpl(
    private val api: ITunesApi
) : TracksRepository {

override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow { //использование корутин и Flow и suspend функции для работы с потоком
    emit(Resource.Loading)
    try {
        val response = api.search(query)
        val trackList = response.results.map { it.toDomain() }
        emit(Resource.Success(trackList))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: "Ошибка при загрузке"))
    }
}/*.flowOn(Dispatchers.IO)*/ // гарантия выполнения в IO-пуле, пока не делал

    private fun TrackDTO.toDomain() = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,//formatDuration(trackTimeMillis),
        artworkUrl100 = artworkUrl100,
        previewUrl = previewUrl,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country
    )
}