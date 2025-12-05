package com.example.playlistmaker.search.data.converters

import com.example.playlistmaker.search.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConvertor {

    fun map (track: Track): TrackEntity {
        return TrackEntity(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.previewUrl, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, isFavorite = track.isFavorite, addedAt = System.currentTimeMillis())
    }

    fun map (track: TrackEntity): Track {
        return Track(track.trackId, track.trackName, track.artistName, track.trackTimeMillis, track.artworkUrl100, track.previewUrl, track.collectionName, track.releaseDate, track.primaryGenreName, track.country, track.isFavorite) //  isFavorite = true для корректного получения инфы из базы
    }
}