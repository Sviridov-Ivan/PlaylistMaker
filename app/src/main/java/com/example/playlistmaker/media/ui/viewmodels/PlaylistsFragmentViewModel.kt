package com.example.playlistmaker.media.ui.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsFragmentViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    // StateFlow со списком плейлистов (использую вместо LiveData - рекомендация наставника)
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    // StateFlow для состояния плейсхолдера
    private val _placeholderState = MutableStateFlow<PlaceholderPlaylistsState>(PlaceholderPlaylistsState.None)
    val placeholderState: StateFlow<PlaceholderPlaylistsState> = _placeholderState.asStateFlow()

    fun showPlaylists() {
        viewModelScope.launch {
            interactor.getPlaylists().collect { playlistsList ->
                if (playlistsList.isNullOrEmpty()) {
                    _playlists.value = emptyList()
                    _placeholderState.value = PlaceholderPlaylistsState.Empty

                } else {
                    _playlists.value = playlistsList
                    _placeholderState.value = PlaceholderPlaylistsState.None
                }
            }
        }
    }
    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderPlaylistsState {
        object None : PlaceholderPlaylistsState() // ничего не показывать
        object Empty : PlaceholderPlaylistsState() // "ничего не найдено"

    }
}