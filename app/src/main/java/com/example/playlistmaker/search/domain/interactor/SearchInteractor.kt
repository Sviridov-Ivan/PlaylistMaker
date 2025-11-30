package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.data.db.AppDatabase
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository

class SearchInteractor(
    private val repository: SearchHistoryRepository,
    private val appDatabase: AppDatabase
) {

    fun saveTrack(track: Track) {
        repository.saveTrack(track)
    }

    suspend fun getHistory(): List<Track> {
        val history = repository.getHistory()
        // Список избранных ID из базы данных
        val favouriteIds = appDatabase.trackDao().getFavoriteTrackIds()
        // Избранные
        history.forEach { it.isFavorite = it.trackId in favouriteIds}

        return history

    }

    fun clearHistory() {
        repository.clearHistory()
    }
}