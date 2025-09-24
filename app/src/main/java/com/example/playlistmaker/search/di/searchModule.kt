package com.example.playlistmaker.search.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.interactor.SearchInteractor
import com.example.playlistmaker.search.domain.interactor.TracksInteractor
import com.example.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.ui.SearchViewModel
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {

    // data
    // Network
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    // Gson
    single { Gson() }

    // SharedPreferences
    single<SharedPreferences> {
        androidContext().getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    }

    // Repositories
    single<TracksRepository> { TracksRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(), get()) }

    // domain
    // Interactor
    factory { TracksInteractor(get()) }
    factory { SearchInteractor(get()) }

    // ui
    // viewModel
    viewModel { SearchViewModel(get(), get()) }

}