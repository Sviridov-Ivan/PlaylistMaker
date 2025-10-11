package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.ui.viewmodels.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.media.ui.viewmodels.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {

    //ui
    viewModel { FavouriteTracksFragmentViewModel() }
    viewModel { PlaylistsFragmentViewModel() }

}
