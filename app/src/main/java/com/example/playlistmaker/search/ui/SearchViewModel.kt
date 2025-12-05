package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.launch

// управление состоянием экрана поиска
class SearchViewModel(
    private val trackInteractor: TracksInteractor, // интерактор для работы с API поиска треков
    private val searchInteractor: SearchInteractor // интерактор для работы с историей поиска
) : ViewModel() {

    // LiveData со списком треков (результаты поиска или история)
    private val tracksLiveData = MutableLiveData<List<Track>>()
    fun observeTracks(): LiveData<List<Track>> = tracksLiveData

    // LiveData для отображения загрузки (показываем ProgressBar)
    private val isLoadingLiveData = MutableLiveData<Boolean>()
    fun observeLoading(): LiveData<Boolean> = isLoadingLiveData

    // LiveData для состояния плейсхолдера (пусто, ошибка, история или ничего)
    private val placeholderStateLiveData = MutableLiveData<PlaceholderState>()
    fun observePlaceholderState(): LiveData<PlaceholderState> = placeholderStateLiveData

    //  Поиск треков по запросу с использованием корутин и FLow
    fun searchTracks(query: String) {
        if (query.isEmpty()) return // если строка пустая, возращаем

        viewModelScope.launch { // запуск потока
            trackInteractor.searchTracks(query).collect { result ->
                when (result) {

                    is Resource.Loading -> {
                        isLoadingLiveData.postValue(true)
                        placeholderStateLiveData.postValue(PlaceholderState.None)
                    }

                    is Resource.Success -> {
                        isLoadingLiveData.postValue(false)
                        val tracks = result.data
                        if (tracks.isNotEmpty()) {
                            tracksLiveData.postValue(tracks)
                            placeholderStateLiveData.postValue(PlaceholderState.None)
                        } else {
                            tracksLiveData.postValue(emptyList())
                            placeholderStateLiveData.postValue(PlaceholderState.Empty)
                        }
                    }

                    is Resource.Error -> {
                        isLoadingLiveData.postValue(false)
                        tracksLiveData.postValue(emptyList())
                        placeholderStateLiveData.postValue(PlaceholderState.Error)
                    }
                }
            }
        }
    }

    // Сохранить трек в историю поиска
    fun saveTrack(track: Track) {
        searchInteractor.saveTrack(track)
    }

    // Показать историю поиска
    fun showHistory() {
        viewModelScope.launch { // запускаем в отдельном потоке, так как getHistory suspend (теперь асинхронный)
            val history = searchInteractor.getHistory()
            if (history.isNotEmpty()) {
                tracksLiveData.postValue(history) // показываем историю
                placeholderStateLiveData.postValue(PlaceholderState.History)
            } else {
                tracksLiveData.postValue(emptyList())
                placeholderStateLiveData.postValue(PlaceholderState.None) // скрываем плейсхолдер
            }
        }
    }

    // Очистить историю поиска
    fun clearHistory() {
        searchInteractor.clearHistory()
        showHistory() // сразу обновляем UI
    }

    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderState {
        object None : PlaceholderState() // ничего не показывать
        object Empty : PlaceholderState() // "ничего не найдено"
        object Error : PlaceholderState() // ошибка сети / сервера
        object History : PlaceholderState() // показать историю поиска
    }
}