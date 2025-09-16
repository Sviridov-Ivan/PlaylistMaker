package com.example.playlistmaker.player.domain.interactor

import com.example.playlistmaker.player.domain.model.PlayerState

interface AudioPlayerInteractor {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun getState(): PlayerState
    fun currentPosition(): Int
}