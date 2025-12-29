package com.example.playlistmaker.media.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.utils.ShareUtils
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.sharing.data.ExternalNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.String

class PlaylistFragmentViewModel(
    private val interactor: PlaylistInteractor,
    private val externalNavigator: ExternalNavigator
) : ViewModel() {

    // StateFlow с состоянием шеринга плейлиста
    private val _shareState = MutableStateFlow<PlaylistShareState>(PlaylistShareState.Idle)
    val shareState: StateFlow<PlaylistShareState> = _shareState.asStateFlow()

    // StateFlow со списком треков в плейлисте (BottomSheet с треками)
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    // StateFlow для состояния плейсхолдера в BottomSheet для отображения треков плейлиста
    private val _placeholderTracksState =
        MutableStateFlow<PlaceholderTracksStateInPlaylist>(PlaceholderTracksStateInPlaylist.Empty)
    val placeholderTracksState: StateFlow<PlaceholderTracksStateInPlaylist> = _placeholderTracksState.asStateFlow()

    // StateFlow для плейлиста
    private val _playlist = MutableStateFlow<Playlist?>(null)
    val playlist: StateFlow<Playlist?> = _playlist.asStateFlow()

    // StateFlow для суммы времени треков
    private val _totalDuration = MutableStateFlow(0)
    val totalDuration: StateFlow<Int> = _totalDuration.asStateFlow()

    // StateFlow для статуса удаления плейлиста
    private val _deleteState = MutableStateFlow<PlaylistDeleteState?>(null)
    val deleteState = _deleteState.asStateFlow()

    // загрузка плейлиста
    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)
            _playlist.value = playlist

            playlist?.let {
                loadDuration(it.trackIds)
            }
        }
    }

    // получение функции суммирования времени из интерактора и запуска в новом потоке (подписка на длительность)
    private fun loadDuration(trackIds: List<String>) {
        viewModelScope.launch {
            interactor.getTotalDurationMinutes(trackIds).collect { minutes ->
                _totalDuration.value = minutes
            }
        }
    }

    // получение треков в конкретном плейлисте из интерактора для отображения в BottomSheet с треками
    fun showPlaylistsBottomSheet(trackIds: List<String>) {
        viewModelScope.launch {
            interactor.getTracksByIdsFromPlaylists(trackIds).collect { tracks ->
                if (tracks.isEmpty()) {
                    _tracks.value = emptyList()
                    _placeholderTracksState.value = PlaceholderTracksStateInPlaylist.Empty

                } else {
                    _tracks.value = tracks
                    _placeholderTracksState.value = PlaceholderTracksStateInPlaylist.NotEmpty
                }
            }
        }
    }

    // удаление трека из плейлиста
    fun deleteTrackFromPlaylist(track: Track) {
        val playlistId = _playlist.value?.id ?: return // если плейлист ещё не загружен — return
        viewModelScope.launch {
            interactor.removeTrackFromPlaylist(playlistId, track.trackId.toString())
        }
    }

    // шеринг плейлиста
    fun sharePlaylist() {
        val currentPlaylist = _playlist.value
        val playlistTracks = _tracks.value
        if (currentPlaylist == null || playlistTracks.isEmpty()) { // проверка на наличие хоть одного трека
            _shareState.value = PlaylistShareState.ShowMessage(
                "В этом плейлисте нет списка треков, которым можно поделиться"
            )
            return
        }

        val textToShare = ShareUtils.formatPlaylistForSharing(
            currentPlaylist.name,
            currentPlaylist.description,
            playlistTracks
        )

        _shareState.value = PlaylistShareState.ShareText(textToShare) // состояние отправки
    }

//    fun handleShareText(text: String) { // вызов функции отрпавки из Навигатора
//        externalNavigator.shareText(text)
//    }

    fun resetShareState() { // сброс состояния
        _shareState.value = PlaylistShareState.Idle
    }

    // функция статуса УДАЛЕНИЯ плейлиста
    fun confirmPlaylistDelete() {
        val currentPlaylist = _playlist.value ?: return

        viewModelScope.launch {
            try {
                interactor.removePlaylist(currentPlaylist)

                _deleteState.value = PlaylistDeleteState.Deleted

            } catch (e: Exception) {
                _deleteState.value =
                    PlaylistDeleteState.Error("Не удалось удалить плейлист")
            }
        }
    }

    // функция обработки нажатия на элемент УДАЛИТЬ плейлист в BottomSheet Меню с вызовом диалога
    fun onDeletePlaylistClicked() {
        val playlist = _playlist.value ?: return
        _deleteState.value = PlaylistDeleteState.ShowConfirmDialog(playlist.name)
    }

    // состояния для отображения плейсхолдера в BottomSheet треков плейлиста
    sealed class PlaceholderTracksStateInPlaylist {
        object Empty : PlaceholderTracksStateInPlaylist() // "ничего не найдено"
        object NotEmpty : PlaceholderTracksStateInPlaylist() // показать избранные треки

    }

    // состояние и статус отправки данных ПОДЕЛИТЬСЯ
    sealed interface PlaylistShareState {
        object Idle : PlaylistShareState
        data class ShowMessage(val message: String) : PlaylistShareState
        data class ShareText(val text: String) : PlaylistShareState
    }

    // состнояние и статус удаления альбома
    sealed interface PlaylistDeleteState {

        // показать диалог подтверждения
        data class ShowConfirmDialog(val playlistName: String) : PlaylistDeleteState

        // показать тост + закрыть экран
        data object Deleted : PlaylistDeleteState

        // ошибка удаления
        data class Error(val message: String) : PlaylistDeleteState
    }
}


