package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    companion object {
        private const val KEY_DARK_THEME = "dark_theme" // ключ для Shared Preferences
    }

    override fun isDarkThemeEnable(): Boolean {
        return sharedPreferences.getBoolean(KEY_DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}