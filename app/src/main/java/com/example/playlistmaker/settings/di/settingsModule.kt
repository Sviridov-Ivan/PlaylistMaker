package com.example.playlistmaker.settings.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingModule = module {

    // data
    // SharedPreferences для настроек
    single<SharedPreferences>(named("settings_prefs")) {
        androidContext().getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    }

    // Repository
    single<SettingsRepository> {
        SettingsRepositoryImpl(get(named("settings_prefs")))
    }

    // domain
    // Interactor
    single { SettingsInteractor(get()) }

    // ui
    viewModel {
        SettingsViewModel(
            sharingInteractor = get(), // интерфейсы
             settingsInteractor = get()
        )
    }
}