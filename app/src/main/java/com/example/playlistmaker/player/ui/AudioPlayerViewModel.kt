package com.example.playlistmaker.player.ui

import androidx.lifecycle.*
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.interactor.FavouriteTracksInteractor
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.formatDuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val favouriteTracksInteractor: FavouriteTracksInteractor, // для избранных треков
    private val track: Track // для избранных треков

) : ViewModel() {

    // LiveData
    val playerStateLiveData = MutableLiveData<PlayerState>() // убрал private - использую в onPause AudioPlayerActivity
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val currentTimeLiveData = MutableLiveData(formatDuration(0L))
    fun observeCurrentTime(): LiveData<String> = currentTimeLiveData

    private val toastMessageLiveData = MutableLiveData<String?>()
    fun observeToastMessage(): LiveData<String?> = toastMessageLiveData

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

    fun prepare(url: String) {
        interactor.prepare(
            url,
            onPrepared = {
                playerStateLiveData.postValue(PlayerState.PREPARED)
            },
            onCompletion = {
                stopTimerUpdates() // завершаю работу корутин
                playerStateLiveData.postValue(PlayerState.PREPARED)
                currentTimeLiveData.postValue(formatDuration(0L))
            }
        )
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            PlayerState.PLAYING -> pausePlayback()
            PlayerState.PREPARED, PlayerState.PAUSED -> startPlayback()
            else -> {}
        }
    }

    private fun startPlayback() {
        interactor.play()
        playerStateLiveData.postValue(PlayerState.PLAYING)
        startTimerUpdates()
    }

    private fun pausePlayback() {
        interactor.pause()
        playerStateLiveData.postValue(PlayerState.PAUSED)
        stopTimerUpdates()
    }

    private fun startTimerUpdates() {
        stopTimerUpdates() // воизбежании дублирования запуска корутин

        timerJob = viewModelScope.launch { // запуск таймера в потоке (Dispatchers.Main)
            while (isActive) { // свойство медиаплейера
                currentTimeLiveData.value = formatDuration(interactor.currentPosition().toLong())
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
        interactor.release()
        playerStateLiveData.postValue(PlayerState.DEFAULT)
    }

     // для работы с избранными треками (обработчик нажатия на иконку избранных)
    fun onFavoriteClicked() {
        viewModelScope.launch {
            val newFavoriteStatus = !track.isFavorite
            // обновляем базу через интерактор
            if (newFavoriteStatus) {
                favouriteTracksInteractor.addToFavourites(track)
            } else {
                favouriteTracksInteractor.removeFromFavouriteTrack(track)
            }

            // обновляем локальный объект и LiveData для UI
            track.isFavorite = newFavoriteStatus
            isFavouriteLiveData.postValue(newFavoriteStatus)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 300L

    }
}