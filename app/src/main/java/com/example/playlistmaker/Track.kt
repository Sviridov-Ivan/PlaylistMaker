package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackId: Long,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека миллисекундах, так приходит от база iTunes Search API.
    val artworkUrl100: String // Ссылка на изображение обложки
) /*{
    val formattedTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime) // функция для преобразования продолжительности трека из миллисекунд в необходимый формат, наверняка можно и в другой
}*/ // что-то не сработало, поэтому создаю отдельный файл кт
