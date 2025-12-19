package com.example.playlistmaker.media.domain.model

data class NewPlaylist( // для создания нового альбома
    val name: String,
    val description: String?,
    val artworkPath: String?
)
