package com.example.playlistmaker.search.domain.interactor

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.FavouriteTracksRepository

class FavouriteTracksInteractor(private val repository: FavouriteTracksRepository) {

    suspend fun addToFavourites(track: Track) {
        repository.addToFavourites(track)
    }

    suspend fun removeFromFavouriteTrack(track: Track){
        repository.removeFromFavouriteTrack(track)
    }

    fun getFavouriteTracks() = repository.getFavouriteTracks()

}

