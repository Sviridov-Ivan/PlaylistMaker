package com.example.playlistmaker.player.ui

import androidx.lifecycle.*
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.util.formatDuration
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor
) : ViewModel() {

    // LiveData
    val playerStateLiveData = MutableLiveData<PlayerState>() // убрал private - использую в onPause AudioPlayerActivity
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val currentTimeLiveData = MutableLiveData(formatDuration(0L))
    fun observeCurrentTime(): LiveData<String> = currentTimeLiveData

    private val toastMessageLiveData = MutableLiveData<String?>()
    fun observeToastMessage(): LiveData<String?> = toastMessageLiveData

    private var timerJob: Job? = null // переменная-ссылка на запущенную корутину, выполняющую обновление таймера

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
        interactor.play()
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

    companion object {
        private const val DELAY_MILLIS = 300L

    }
}