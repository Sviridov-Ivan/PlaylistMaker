package com.example.playlistmaker.player.ui

import android.util.Log
import androidx.lifecycle.*
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.service.PlayerServiceController
import com.example.playlistmaker.search.domain.interactor.FavouriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.formatDuration
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val interactorPlaylist: PlaylistInteractor,
    private val interactor: AudioPlayerInteractor,
    private val favouriteTracksInteractor: FavouriteTracksInteractor, // для избранных треков
    private val track: Track, // для избранных треков
    private val analytics: FirebaseAnalytics // для сбора аналитики

) : ViewModel() {

    // переменная/флаг для определения свернут/не свернут плейер
    private var isUiVisible = false

    // экземпляр PlayerServiceController для передачи команд в сервис для Плейера
    private var audioPlayerControl: PlayerServiceController? = null

    fun setAudioPlayerControl(serviceController: PlayerServiceController) {
        audioPlayerControl = serviceController

        // состояние сервиса
        viewModelScope.launch {
            serviceController.playerState.collect { state ->
                playerStateLiveData.postValue(state)
                //updateForegroundState()
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    // одноразовое событие для работы с BottomSheet в зависимости от UiEvent
    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents = _uiEvents.asSharedFlow()

    // StateFlow со списком плейлистов (использую вместо LiveData - рекомендация наставника)
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    // LiveData
    val playerStateLiveData = MutableLiveData<PlayerState>() // убрал private - использую в onPause AudioPlayerActivity
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val currentTimeLiveData = MutableLiveData(formatDuration(0L))
    fun observeCurrentTime(): LiveData<String> = currentTimeLiveData

    // StateFlow для тостов (использую вместо LiveData - рекомендация наставника)
    private val _toastMessage = MutableStateFlow<ToastEvent?>(null)
    val toastMessage: StateFlow<ToastEvent?> = _toastMessage.asStateFlow()

    // для работы с избранными треками
    private val isFavouriteLiveData = MutableLiveData<Boolean>()  // MutableLiveData(track.isFavorite)
    fun observeIsFavourite(): LiveData<Boolean> = isFavouriteLiveData

    private var timerJob: Job? = null // переменная-ссылка на запущенную корутину, выполняющую обновление таймера

    init {
        // отслеживаются изменения в базе избранных треков
        viewModelScope.launch {
            favouriteTracksInteractor.getFavouriteTracks().collect { tracks ->
                val isFavorite = tracks.any { it.trackId == track.trackId }
                track.isFavorite = isFavorite
                isFavouriteLiveData.postValue(isFavorite)
            }
        }
    }

    // PLAYER CONTROL (через PlayerService)
    private var isPrepared = false
    fun prepare() {
        Log.d("PLAYER", "prepare called, control = $audioPlayerControl")
        if (isPrepared) return

        audioPlayerControl?.prepare(
            url = track.previewUrl ?: return,
            trackName = track.trackName,
            artistName = track.artistName
        )
        isPrepared = true
    }

    fun playbackControl() {
        Log.d("PLAYER", "state = ${playerStateLiveData.value}")
        when (playerStateLiveData.value) {
            PlayerState.PLAYING -> pausePlayback()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayback()
            else -> {}
        }
    }

    private fun startPlayback() {
        audioPlayerControl?.play()
        startTimerUpdates()
    }

    private fun pausePlayback() {
        audioPlayerControl?.pause()
        stopTimerUpdates()
    }

    private fun startTimerUpdates() {
        stopTimerUpdates() // воизбежании дублирования запуска корутин

        timerJob = viewModelScope.launch { // запуск таймера в потоке (Dispatchers.Main)
            while (isActive) { // свойство медиаплейера
                val pos = audioPlayerControl?.currentPosition() ?: 0
                currentTimeLiveData.postValue(formatDuration(pos.toLong()))
                delay(DELAY_MILLIS)
            }
        }
    }
    private fun stopTimerUpdates() {
        timerJob?.cancel() // отмена
        timerJob = null
    }

    fun updateTime(){
        currentTimeLiveData.postValue(formatDuration(interactor.currentPosition().toLong()))
    }

    fun release() {
        stopTimerUpdates()
        audioPlayerControl?.release()
        playerStateLiveData.postValue(PlayerState.DEFAULT)
    }

     // для работы с избранными треками (обработчик нажатия на иконку избранных)
    fun onFavoriteClicked() {
        viewModelScope.launch {
            val newFavoriteStatus = !track.isFavorite
            // обновляем базу через интерактор
            if (newFavoriteStatus) {
                favouriteTracksInteractor.addToFavourites(track)

                // при добавлении трека в избранные - добавление в аналитику
                analytics.logEvent("add_to_favorite") {
                    param("track_id", track.trackId)
                    param("track_name", track.trackName)
                }
            } else {
                favouriteTracksInteractor.removeFromFavouriteTrack(track)

                // при удалении трека из избранных - добавление в аналитику
                analytics.logEvent("remove_from_favorite") {
                    param("track_id", track.trackId)
                    param("track_name", track.trackName)
                }
            }

            // обновляем локальный объект и LiveData для UI
            track.isFavorite = newFavoriteStatus
            isFavouriteLiveData.postValue(newFavoriteStatus)
        }
    }

    // получение плейлистов из интерактора для отображения в BottomSheet
    fun showPlaylistsBottomSheet() {
        viewModelScope.launch {
            interactorPlaylist.getPlaylists().collect { playlistsList ->
                if (playlistsList.isNullOrEmpty()) {
                    _playlists.value = emptyList()

                } else {
                    _playlists.value = playlistsList
                }
            }
        }
    }

    // функция для работы с добавлением трека в плейлист и отображения тостов
    fun onPlaylistClicked(playlist: Playlist) {
        if (playlist.trackIds.contains(track.trackId.toString())) { // проверка на наличие трека в альбоме
            _toastMessage.value = ToastEvent.AlreadyExists(playlist.name) // тост при наличии трека
            return
        }

        viewModelScope.launch {
            interactorPlaylist.addTrackToPlaylist(playlist, track) // добавление трека в альбом, если его нет
            _toastMessage.value = ToastEvent.Added(playlist.name) // тост при добавлении
            _uiEvents.emit(UiEvent.HideBottomSheet) // команда для BottomSheet
        }
    }

    // функция для очистки тостов
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    // класс для тоста для использования в StateFlow
    sealed class ToastEvent(val playlistName: String) {
        class Added(name: String) : ToastEvent(name)
        class AlreadyExists(name: String) : ToastEvent(name)
    }

    // отдельное UI-событие для BottomSheet
    sealed class UiEvent {
        object HideBottomSheet : UiEvent()
    }

    companion object {
        private const val DELAY_MILLIS = 300L

    }
}