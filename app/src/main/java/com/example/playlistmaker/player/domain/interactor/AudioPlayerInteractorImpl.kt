package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {
    private var state: PlayerState = PlayerState.DEFAULT

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(url, {
            state = PlayerState.PREPARED
            onPrepared()
        }, {
            state = PlayerState.PREPARED
            onCompletion()
        })
    }

    override fun play() {
        repository.start()
        state = PlayerState.PLAYING
    }

    override fun pause() {
        repository.pause()
        state = PlayerState.PAUSED
    }

    override fun release() {
        repository.release()
        state = PlayerState.DEFAULT
    }

    override fun getState(): PlayerState = state

    override fun currentPosition(): Int = repository.currentPosition()
}