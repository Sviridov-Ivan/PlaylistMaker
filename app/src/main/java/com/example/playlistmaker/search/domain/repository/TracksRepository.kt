package com.example.playlistmaker.search.domain.repository


import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(query: String): Flow<Resource<List<Track>>> // использование Flow и корутин (обертка)
}