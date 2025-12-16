package com.example.playlistmaker.media.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val artworkPath: String?,
    val trackIds: List<String>,
    val trackCount: Int
)
