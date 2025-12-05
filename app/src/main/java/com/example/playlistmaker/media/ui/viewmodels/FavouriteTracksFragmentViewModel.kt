package com.example.playlistmaker.media.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.FavouriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavouriteTracksFragmentViewModel(
    private val favouriteTracksInteractor: FavouriteTracksInteractor, // для избранных треков
) : ViewModel() {

    // LiveData со списком треков (результаты поиска для избранных)
    private val tracksFavourLiveData = MutableLiveData<List<Track>>()
    fun observeFavourTracks(): LiveData<List<Track>> = tracksFavourLiveData

    // LiveData для состояния плейсхолдера (пусто, ошибка, история или ничего)
    private val placeholderFavouriteTracksStateLiveData =
        MutableLiveData<PlaceholderFavouriteTracksState>()
    fun observePlaceholderState(): LiveData<PlaceholderFavouriteTracksState> = placeholderFavouriteTracksStateLiveData

    init {
        loadFavourites() // загружается сразу при создании
    }

    // функция для работы с getFavouriteTracks() из бд
    fun loadFavourites() {
        viewModelScope.launch { // запускаем в отдельном потоке
            favouriteTracksInteractor.getFavouriteTracks().collect { tracks ->
                if (tracks.isNotEmpty()) {
                    tracksFavourLiveData.postValue(tracks)
                    placeholderFavouriteTracksStateLiveData.postValue(
                        PlaceholderFavouriteTracksState.Favourites
                    )
                } else {
                    tracksFavourLiveData.postValue(emptyList())
                    placeholderFavouriteTracksStateLiveData.postValue(
                        PlaceholderFavouriteTracksState.Empty
                    )
                }
            }
        }
    }

    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderFavouriteTracksState {
        object Empty : PlaceholderFavouriteTracksState() // "ничего не найдено"
        object Favourites : PlaceholderFavouriteTracksState() // показать избранные треки

    }
}