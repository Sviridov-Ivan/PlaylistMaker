package com.example.playlistmaker.media.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlaylistsFragmentViewModel() : ViewModel() {


    // LiveData для состояния плейсхолдера (пусто, ошибка, история или ничего)
    private val placeholderPlaylistsStateLiveData = MutableLiveData<PlaceholderPlaylistsState>()
    fun observePlaceholderState(): LiveData<PlaceholderPlaylistsState> = placeholderPlaylistsStateLiveData


    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderPlaylistsState {
        object None : PlaceholderPlaylistsState() // ничего не показывать
        object Empty : PlaceholderPlaylistsState() // "ничего не найдено"
        object Error : PlaceholderPlaylistsState() // ошибка сети / сервера

    }
}