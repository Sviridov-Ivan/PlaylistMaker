package com.example.playlistmaker.player.domain.repository

interface AudioPlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun currentPosition(): Int
}