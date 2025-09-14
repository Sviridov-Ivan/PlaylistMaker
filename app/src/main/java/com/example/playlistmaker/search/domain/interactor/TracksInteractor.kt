package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository

class TracksInteractor(private val repository: TracksRepository) {

    fun searchTracks(
        query: String,
        onSuccess: (List<Track>) -> Unit,
        onError: () -> Unit
    ) {
        repository.searchTracks(query, onSuccess, onError)
    }
}