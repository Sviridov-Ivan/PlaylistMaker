package com.example.playlistmaker.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ITunesService {
    private const val iTunesBaseUrl = "https://itunes.apple.com" // базовый Url для запроса треков с itunes

    // создание Retrofit
    private val retrofit = Retrofit.Builder() // переменная для библиотеки Retrofit, которая преобразовывает запросы от сервера из Json в Kotlin
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // созданеие сервиса API
    val api: ITunesApi = retrofit.create(ITunesApi::class.java)
}