package com.example.playlistmaker.creator

import android.content.Context
import android.preference.PreferenceManager
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.search.data.network.ITunesService
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.google.gson.Gson

object Creator {
    fun provideTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(ITunesService.api)
    }

    fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        return SearchHistoryRepositoryImpl(sharedPreferences, gson)
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    fun provideAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideTrackInteractor(): TracksInteractor {
        return TracksInteractor(provideTrackRepository())
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractor(provideSearchHistoryRepository(context))
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractor(provideSettingsRepository(context))
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractor(provideAudioPlayerRepository())
    }


}