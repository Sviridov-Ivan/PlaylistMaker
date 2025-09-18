package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track

interface SearchHistoryRepository {
    fun saveTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}