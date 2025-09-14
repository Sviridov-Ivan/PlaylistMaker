package com.example.playlistmaker.settings.domain.interactor

import com.example.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractor(private val repository: SettingsRepository) {
    fun isDarkModeEnable(): Boolean {
        return repository.isDarkThemeEnable()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}