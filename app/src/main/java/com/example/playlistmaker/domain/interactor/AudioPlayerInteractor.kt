package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.repository.AudioPlayerRepository

class AudioPlayerInteractor(
    private val repository: AudioPlayerRepository
) {
    private var state: PlayerState = PlayerState.DEFAULT

    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, {
            state = PlayerState.PREPARED
            onPrepared()
        }, {
            state = PlayerState.PREPARED  // после окончания снова "готов"
            onCompletion()
        })
    }

    fun playbackControl() {
        when (state) {
            PlayerState.PLAYING -> {
                repository.pause()
                state = PlayerState.PAUSED // eсли текущее состояние медиаплеера равно STATE_PLAYING, то нажатие на кнопку должно ставить воспроизведение на паузу (вызываем функцию pausePlayer())
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                repository.start()
                state = PlayerState.PLAYING // если текущее состояние STATE_PAUSED или STATE_PREPARED, то нажатие на кнопку должно запускать воспроизведение (вызываем функцию startPlayer())
            }
            else -> {}
        }
    }

    fun release() {
        repository.release()
        state = PlayerState.DEFAULT
    }

    fun getState(): PlayerState = state

    fun currentPosition(): Int = repository.currentPosition()
}