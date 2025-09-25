package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module { // вызов в Арр

    // MediaPlayer как зависимость
    factory { MediaPlayer() }

    // data
    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }

    // Domain
    factory<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    // UI (ViewModel)
    viewModel { AudioPlayerViewModel(get()) }

}