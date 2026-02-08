package com.example.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.model.PlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerService : Service(), PlayerServiceController {

    // переменные для передачи в уведомление сервиса
    private var trackTitle: String = ""
    private var artistName: String = ""

    private var mediaPlayer: MediaPlayer? = null

    // использование StateFlow для получения потока данных о состоянии плейера
    private val _playerState = MutableStateFlow(PlayerState.DEFAULT)
    override val playerState = _playerState.asStateFlow()
    private val binder = PlayerServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerServiceController/*PlayerService*/ = this@PlayerService
    }

    // создание канала уведомления с проверкой версионности
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // создание самого уведомления foreground
    private fun createNotification(): Notification {
        val contentText = "$artistName - $trackTitle"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Playlist Maker")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    // функция запуска сервиса foreground
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FOREGROUND", "onStartCommand CALLED")

        return START_NOT_STICKY
    }

    // Методы управления Media Player
    override fun prepare(url: String, trackName: String, artistName: String) {
        this.trackTitle = trackName
        this.artistName = artistName

        release()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()

            setOnPreparedListener {
                _playerState.value = PlayerState.PREPARED

            }

            setOnCompletionListener {
                mediaPlayer?.seekTo(0) // обнуляем таймер при окончании трека
                _playerState.value = PlayerState.PREPARED
                stopForeground(STOP_FOREGROUND_REMOVE)
            }
        }
    }

    override fun play() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            _playerState.value = PlayerState.PLAYING

            createNotificationChannel()

            startForeground(
                NOTIFICATION_ID,
                createNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        }
    }

    override fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _playerState.value = PlayerState.PAUSED

            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    override fun currentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerState.DEFAULT
    }

    override fun onDestroy() {
        release()
        super.onDestroy()
    }

    companion object {
        private const val CHANNEL_ID = "player_channel"
        private const val NOTIFICATION_ID = 100
    }
}