package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class TracksInteractor(private val repository: TracksRepository) {

    fun searchTracks(
        query: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(query)
    }
}