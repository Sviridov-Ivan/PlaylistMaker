package com.example.playlistmaker.player.service

import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.StateFlow

interface PlayerServiceController {

    val playerState: StateFlow<PlayerState>

    fun prepare(track: Track)
    fun play()
    fun pause()
    fun release()

    fun currentPosition(): Int

    fun startForegroundIfPlaying()

    fun stopForegroundIfNeed()

    fun getCurrentState(): PlayerState

}