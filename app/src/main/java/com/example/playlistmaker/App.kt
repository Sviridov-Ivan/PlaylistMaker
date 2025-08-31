package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.domain.interactor.SettingsInteractor
import com.example.playlistmaker.domain.repository.SettingsRepository

class App : Application() {

    val settingsInteractor: SettingsInteractor by lazy { Creator.provideSettingsInteractor(this) }


    override fun onCreate() {
        super.onCreate()

        switchTheme(settingsInteractor.isDarkModeEnable())
    }
    fun switchTheme(darkThemeEnabled: Boolean) {

        // Применяем тему (без этого не меняет тему через свитч)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}

