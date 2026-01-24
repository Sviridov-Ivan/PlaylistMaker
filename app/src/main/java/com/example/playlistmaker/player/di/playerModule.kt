package com.example.playlistmaker.player.di


import android.media.MediaPlayer
import com.example.playlistmaker.player.data.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interactor.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.repository.AudioPlayerRepository
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import com.example.playlistmaker.search.domain.model.Track
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext

val playerModule = module { // вызов в Арр

    // MediaPlayer как зависимость
    single { MediaPlayer() } // один и тот же плейер

    // data
    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl() }

    // Domain
    factory<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    single<FirebaseAnalytics> {
        FirebaseAnalytics.getInstance(androidContext())
    }

    // UI (ViewModel)
    // ViewModel с передачей трека напрямую через параметры
    viewModel { (track: Track) ->
        AudioPlayerViewModel(get(), get(), get(), track, analytics = get())
    }
}