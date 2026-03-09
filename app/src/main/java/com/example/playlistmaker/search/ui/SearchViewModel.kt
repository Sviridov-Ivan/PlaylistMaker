package com.example.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.model.PlaceholderState
import com.example.playlistmaker.search.ui.model.SearchUiState
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// управление состоянием экрана поиска
class SearchViewModel(
    private val trackInteractor: TracksInteractor, // интерактор для работы с API поиска треков
    private val searchInteractor: SearchInteractor // интерактор для работы с историей поиска
) : ViewModel() {

    init {
        showHistory()
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null



    fun onQueryChanged(text: String) {

        _uiState.update {
            it.copy(query = text)
        }

        if (text.isEmpty()) {
            showHistory()
            return
        }

        searchDebounce(text)
    }

    private fun searchDebounce(text: String) {

        searchJob?.cancel()

        searchJob = viewModelScope.launch {

            delay(SEARCH_DEBOUNCE_DELAY)

            searchTracks(text)
        }
    }

    private suspend fun searchTracks(query: String) {

            _uiState.update {
                it.copy(
                    isLoading = true,
                    placeholder = PlaceholderState.None
                )
            }

            trackInteractor.searchTracks(query).collect { result ->

                when (result) {

                    is Resource.Success -> {

                        val tracks = result.data

                        _uiState.update {

                            it.copy(
                                tracks = tracks,
                                isLoading = false,
                                placeholder =
                                    if (tracks.isEmpty())
                                        PlaceholderState.Empty
                                    else
                                        PlaceholderState.None
                            )
                        }
                    }

                    is Resource.Error -> {

                        _uiState.update {

                            it.copy(
                                isLoading = false,
                                placeholder = PlaceholderState.Error
                            )
                        }
                    }

                    else -> {}
                }
            }
    }

    fun onTrackClicked(track: Track) {

        searchInteractor.saveTrack(track)
    }

    fun showHistory() {

        viewModelScope.launch { // запуск suspend функции в отдельном потоке

            val history = searchInteractor.getHistory()

            _uiState.update {

                it.copy(
                    tracks = history,
                    history = history,
                    placeholder =
                        if (history.isEmpty())
                            PlaceholderState.None
                        else
                            PlaceholderState.History
                )
            }
        }
    }

    fun clearHistory() {

        searchInteractor.clearHistory()

        _uiState.update {

            it.copy(
                tracks = emptyList(),
                history = emptyList(),
                placeholder = PlaceholderState.None
            )
        }
    }

    fun retry() {
        val query = _uiState.value.query // сохранение состояние query в переменную

        if (query.isBlank()) return

        viewModelScope.launch { // вызов suspend функции поиска в отдельном потоке
            searchTracks(query)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}