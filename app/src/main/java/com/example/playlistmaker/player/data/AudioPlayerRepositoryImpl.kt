package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository

class AudioPlayerRepositoryImpl : AudioPlayerRepository {

    private var mediaPlayer: MediaPlayer? = null

    private fun getOrCreatePlayer(): MediaPlayer {
        return if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!
        } else {
            mediaPlayer!!
        }
    }

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        val player = getOrCreatePlayer()

        try {
            player.reset()
            player.setDataSource(url)
            player.prepareAsync()
            player.setOnPreparedListener { onPrepared() }
            player.setOnCompletionListener { onCompletion() }
        } catch (e: IllegalStateException) {
            // если упали на reset() — создаём новый MediaPlayer
            player.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { onPrepared() }
                setOnCompletionListener { onCompletion() }
            }
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun currentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }
}