package com.example.playlistmaker.settings.domain.repository

interface SettingsRepository {
    fun isDarkThemeEnable(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}