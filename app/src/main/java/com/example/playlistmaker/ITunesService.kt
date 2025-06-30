package com.example.playlistmaker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

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