package com.example.playlistmaker.data.dto

class TracksResponseDTO(
    val searchType: String,
    val expression: String,
    val results: List<TrackDTO> // лучше хранить DTO, а не доменные объекты напрямую
)