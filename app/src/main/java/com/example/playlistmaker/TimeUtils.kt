package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDuration(durationMs: Long): String {
    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
    return dateFormat.format(durationMs)
}