package com.example.playlistmaker.media.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavouriteTracksFragmentViewModel() : ViewModel() {

    // LiveData для состояния плейсхолдера (пусто, ошибка, история или ничего)
    private val placeholderFavouriteTracksStateLiveData =
        MutableLiveData<PlaceholderFavouriteTracksState>()
    fun observePlaceholderState(): LiveData<PlaceholderFavouriteTracksState> = placeholderFavouriteTracksStateLiveData

    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderFavouriteTracksState {
        object None : PlaceholderFavouriteTracksState() // ничего не показывать
        object Empty : PlaceholderFavouriteTracksState() // "ничего не найдено"
        object Error : PlaceholderFavouriteTracksState() // ошибка сети / сервера

    }
}