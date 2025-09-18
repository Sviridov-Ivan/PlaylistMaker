package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.creator.Creator


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Применяем сохранённую тему при старте
        val settingsInteractor = Creator.provideSettingsInteractor(this)
        switchTheme(settingsInteractor.isDarkModeEnable())
    }

    companion object {
        fun switchTheme(darkThemeEnabled: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }
}

