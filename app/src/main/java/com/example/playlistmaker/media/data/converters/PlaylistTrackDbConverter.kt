package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.data.entity.AddTrackToPlaylistEntity
import com.example.playlistmaker.search.domain.model.Track

class PlaylistTrackDbConverter {

    // Track -> AddTrackToPlaylistEntity
    fun map(track: Track): AddTrackToPlaylistEntity {
        return AddTrackToPlaylistEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            previewUrl = track.previewUrl,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            isFavorite = track.isFavorite,
            addedAt = System.currentTimeMillis()
        )
    }

    // AddTrackToPlaylistEntity -> Track
    fun map(entity: AddTrackToPlaylistEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName,
            artistName = entity.artistName,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100,
            previewUrl = entity.previewUrl,
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            isFavorite = entity.isFavorite
        )
    }
}
