package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchInteractor(private val repository: SearchHistoryRepository) {

    fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    fun clearHistory() {
        repository.clearHistory()
    }
}