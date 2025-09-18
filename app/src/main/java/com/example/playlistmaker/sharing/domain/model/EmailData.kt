package com.example.playlistmaker.sharing.domain.model

data class EmailData( //модель для письма
    val email: String,
    val subject: String,
    val body: String
)