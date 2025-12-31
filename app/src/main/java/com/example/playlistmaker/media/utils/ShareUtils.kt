package com.example.playlistmaker.media.utils

import com.example.playlistmaker.search.domain.model.Track

// утилита для формирования макета текстового сообщения при ПОДЕЛИТЬСЯ во фрагменте плейлиста
object ShareUtils {

    fun formatPlaylistForSharing(
        playlistName: String,
        playlistDescription: String?,
        tracks: List<Track>
    ): String {
        if (tracks.isEmpty()) return ""

        val trackCount = tracks.size
        val trackWord = when {
            trackCount % 10 == 1 && trackCount % 100 != 11 -> "трек"
            trackCount % 10 in 2..4 && trackCount % 100 !in 12..14 -> "трека"
            else -> "треков"
        }

        val builder = StringBuilder()
        builder.append(playlistName).append("\n")
        if (!playlistDescription.isNullOrBlank()) {
            builder.append(playlistDescription).append("\n")
        }
        builder.append("[$trackCount] $trackWord\n\n")

        tracks.forEachIndexed { index, track ->
            val minutes = (track.trackTimeMillis / 1000) / 60
            val seconds = (track.trackTimeMillis / 1000) % 60
            val timeFormatted = "%d:%02d".format(minutes, seconds)
            builder.append("${index + 1}. ${track.artistName} - ${track.trackName} ($timeFormatted)\n")
        }

        return builder.toString().trim()
    }
}