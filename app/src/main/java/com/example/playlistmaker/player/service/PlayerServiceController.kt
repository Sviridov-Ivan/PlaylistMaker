package com.example.playlistmaker.player.service

import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceController {

    val playerState: StateFlow<PlayerState>

    fun prepare(url: String, trackName: String, artistName: String)
    fun play()
    fun pause()
    fun release()

    fun currentPosition(): Int

//    // foreground Service
//    fun showForeground()
//    fun hideForeground()
}