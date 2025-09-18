package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track

// управление состоянием экрана поиска
class SearchViewModel(
    private val trackInteractor: TracksInteractor, // интерактор для работы с API поиска треков
    private val searchInterator: SearchInteractor // интерактор для работы с историей поиска
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

    //  Поиск треков по запросу
    fun searchTracks(query: String) {
        if (query.isEmpty()) return // если строка пустая, возращаем

        isLoadingLiveData.postValue(true) // прогресс

        trackInteractor.searchTracks(
            query,
            onSuccess = { tracks ->
                isLoadingLiveData.postValue(false) // скрываем прогресс
                if (tracks.isNotEmpty()) {
                    tracksLiveData.postValue(tracks) // обновляем список треков
                    placeholderStateLiveData.postValue(PlaceholderState.None) // убираем плейсхолдер
                } else {
                    tracksLiveData.postValue(emptyList()) // список пустой
                    placeholderStateLiveData.postValue(PlaceholderState.Empty) // показываем "ничего не найдено"
                }
            },
            onError = {
                isLoadingLiveData.postValue(false) // скрываем прогресс
                tracksLiveData.postValue(emptyList()) // очищаем список
                placeholderStateLiveData.postValue(PlaceholderState.Error) // показываем ошибку
            }
        )
    }

    // Сохранить трек в историю поиска
    fun saveTrack(track: Track) {
        searchInterator.saveTrack(track)
    }

    // Показать историю поиска
    fun showHistory() {
        val history = searchInterator.getHistory()
        if (history.isNotEmpty()) {
            tracksLiveData.postValue(history) // показываем историю
            placeholderStateLiveData.postValue(PlaceholderState.History)
        } else {
            tracksLiveData.postValue(emptyList())
            placeholderStateLiveData.postValue(PlaceholderState.None) // скрываем плейсхолдер
        }
    }

    // Очистить историю поиска
    fun clearHistory() {
        searchInterator.clearHistory()
        showHistory() // сразу обновляем UI
    }

    // Состояния для отображения плейсхолдеров на экране
    sealed class PlaceholderState {
        object None : PlaceholderState() // ничего не показывать
        object Empty : PlaceholderState() // "ничего не найдено"
        object Error : PlaceholderState() // ошибка сети / сервера
        object History : PlaceholderState() // показать историю поиска
    }

    // Фабрика для создания SearchViewModel с передачей зависимостей вручную
    companion object {
        fun provideFactory(
            trackInteractor: TracksInteractor,
            searchInteractor: SearchInteractor
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(trackInteractor, searchInteractor) as T
                }
            }
        }
    }
}