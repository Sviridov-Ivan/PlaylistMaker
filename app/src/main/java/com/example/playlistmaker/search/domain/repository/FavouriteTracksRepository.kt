package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTracksRepository {

    suspend fun addToFavourites(track: Track)

    suspend fun removeFromFavouriteTrack(track: Track)

    fun getFavouriteTracks(): Flow<List<Track>>

}