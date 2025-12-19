package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.di.databaseModule
import com.example.playlistmaker.media.di.mediaModule
import com.example.playlistmaker.player.di.playerModule
import com.example.playlistmaker.search.di.searchModule
import com.example.playlistmaker.settings.di.settingModule
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.di.sharingModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Запуск Koin
        startKoin {
            androidContext(this@App)
            modules(
                playerModule,
                           searchModule,
                           settingModule,
                           sharingModule,
                           mediaModule,
                           databaseModule
            )
        }

        // Применяем сохранённую тему при старте
        val settingsInteractor: SettingsInteractor = getKoin().get()
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

