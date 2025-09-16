package com.example.playlistmaker.player.ui

import androidx.lifecycle.*
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.util.formatDuration

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

    fun prepare(url: String) {
        interactor.prepare(
            url,
            onPrepared = {
                playerStateLiveData.postValue(PlayerState.PREPARED)
            },
            onCompletion = {
                playerStateLiveData.postValue(PlayerState.PREPARED)
                currentTimeLiveData.postValue(formatDuration(0L))
            }
        )
    }

    fun playbackControl() {
        when (playerStateLiveData.value) {
            PlayerState.PLAYING -> {
                interactor.pause()
                playerStateLiveData.postValue(PlayerState.PAUSED)
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                interactor.play()
                playerStateLiveData.postValue(PlayerState.PLAYING)
            }
            else -> {}
        }
    }

    fun updateTime(){
        currentTimeLiveData.postValue(formatDuration(interactor.currentPosition().toLong()))
    }

    fun release() {
        interactor.release()
        playerStateLiveData.postValue(PlayerState.DEFAULT)
    }

    // Factory для ViewModel
    companion object {
        fun getFactory(interactor: AudioPlayerInteractor): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AudioPlayerViewModel(interactor) as T
                }
            }
        }
    }
}