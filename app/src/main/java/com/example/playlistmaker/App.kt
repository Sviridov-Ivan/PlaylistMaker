package com.example.playlistmaker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor
        private set

    lateinit var sharingInteractor: SharingInteractor
        private set

    override fun onCreate() {
        super.onCreate()

        // Репозиторий + интерактор для настроек
        val prefs = getSharedPreferences("playlist_prefs", Context.MODE_PRIVATE)
        val settingsRepo = SettingsRepositoryImpl(prefs)
        settingsInteractor = SettingsInteractor(settingsRepo)

        // Интерактор для шеринга
        sharingInteractor = SharingInteractorImpl(this)

        // Применяем сохранённую тему при старте
        switchTheme(settingsInteractor.isDarkModeEnable())
    }

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
