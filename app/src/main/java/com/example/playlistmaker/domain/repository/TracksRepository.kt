package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TracksRepository {
    fun searchTracks(
        query: String,
        onSuccess: (List<Track>) -> Unit,
        onError: () -> Unit
    )
}