package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.data.entity.AddTrackToPlaylistEntity
import com.example.playlistmaker.search.domain.model.Track

class PlaylistTrackDbConverter {
    fun map(track: Track): AddTrackToPlaylistEntity { // обратная конвертация не нужна - не извлекаю AddTrackToPlaylist обратно в Track
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
}