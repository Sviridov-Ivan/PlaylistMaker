package com.example.playlistmaker.util

object DebounceConfig {
    // Временные параметры дебонса
    const val CLICK_DEBOUNCE_DELAY = 1000L // задержка на открытия активити AudioPlayerActivity
    const val SEARCH_DEBOUNCE_DELAY = 2000L // задержка для начала отправки запроса после введенного текста в строке поиска
}