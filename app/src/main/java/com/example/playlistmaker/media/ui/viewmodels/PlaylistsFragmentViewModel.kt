package com.example.playlistmaker.media.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.ui.model.PlaylistsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.toImmutableList

class PlaylistsFragmentViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    // StateFlow со списком плейлистов (использую вместо LiveData - рекомендация наставника)
    private val _uiState = MutableStateFlow(PlaylistsScreenState())
    val uiState: StateFlow<PlaylistsScreenState> = _uiState.asStateFlow()

    fun showPlaylists() {
        viewModelScope.launch {
            interactor.getPlaylists().collect { playlistsList ->
                _uiState.value = PlaylistsScreenState(
                    playlists = playlistsList.toImmutableList(),
                    isPlaceholderVisible = playlistsList.isEmpty()
                )
            }
        }
    }
}