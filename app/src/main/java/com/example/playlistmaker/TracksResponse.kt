package com.example.playlistmaker

class TracksResponse(
    val searchType: String,
    val expression: String,
    val results: List<Track>
)
