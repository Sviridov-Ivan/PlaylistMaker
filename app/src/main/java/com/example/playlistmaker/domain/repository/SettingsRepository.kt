package com.example.playlistmaker.domain.repository

interface SettingsRepository {
    fun isDarkThemeEnable(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}