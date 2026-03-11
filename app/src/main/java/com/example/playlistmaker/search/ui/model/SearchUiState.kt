package com.example.playlistmaker.search.ui.model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.search.domain.model.Track

@Immutable
data class SearchUiState(
    val query: String = "",

    val tracks: List<Track> = emptyList(),

    val isLoading: Boolean = false,

    val placeholder: PlaceholderState = PlaceholderState.None,

    val history: List<Track> = emptyList()
)

enum class PlaceholderState {
    None,
    Empty,
    Error,
    History
}

