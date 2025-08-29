package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractor(private val repository: SettingsRepository) {
    fun isDarkModeEnable(): Boolean {
        return repository.isDarkThemeEnable()
    }

    fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}