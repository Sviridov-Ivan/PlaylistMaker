package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.converters.PlayListDbConverter
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.media.data.repository.PlaylistsRepositoryImpl
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.repository.PlaylistRepository
import com.example.playlistmaker.media.ui.viewmodels.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.media.ui.viewmodels.NewPlaylistViewModel
import com.example.playlistmaker.media.ui.viewmodels.PlaylistsFragmentViewModel
import com.google.gson.Gson
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    // data
    // Gson
    single { Gson() }

    // Converters
    single { PlayListDbConverter(get()) }
    single { PlaylistTrackDbConverter() }

    // Repositories
    single<PlaylistRepository> { PlaylistsRepositoryImpl(get(), get(), get()) }

    // domain
    // Interactor
    factory { PlaylistInteractor(get()) }


    //ui
    viewModel { NewPlaylistViewModel(get()) }
    viewModel { FavouriteTracksFragmentViewModel(get()) }
    viewModel { PlaylistsFragmentViewModel(get()) }

}
