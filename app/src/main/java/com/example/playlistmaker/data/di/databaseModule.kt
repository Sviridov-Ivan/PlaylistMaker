package com.example.playlistmaker.data.di

import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
}