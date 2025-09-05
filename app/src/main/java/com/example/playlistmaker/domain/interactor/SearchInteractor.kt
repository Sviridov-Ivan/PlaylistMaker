package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.SearchHistoryRepository

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