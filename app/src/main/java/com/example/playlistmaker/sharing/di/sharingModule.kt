package com.example.playlistmaker.sharing.di

import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {

    // data
    // ExternalNavigator
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    // domain
    // SharingInteractor
    single<SharingInteractor> {
        SharingInteractorImpl(androidContext())
    }

}