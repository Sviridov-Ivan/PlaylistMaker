package com.example.playlistmaker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.example.playlistmaker.R


@Composable
fun PlaylistMakerTheme(
    content: @Composable () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            // Стандартные цвета
            primary = colorResource(R.color.blue), // colorPrimary
            onPrimary = colorResource(R.color.white), // colorOnPrimary
            secondary = colorResource(R.color.black), // colorSecondary
            onSecondary = colorResource(R.color.white), // colorOnSecondary
            background = colorResource(R.color.black), // playerBackground / основной фон
            onBackground = colorResource(R.color.white), // читаемый текст на фоне
            surface = colorResource(R.color.black), // основной фон карточек / контейнеров
            onSurface = colorResource(R.color.white), // текст на surface
            // свободные поля для кастомных цветов
            surfaceVariant = colorResource(R.color.white), // colorForMaterialCardView
            tertiary = colorResource(R.color.white), // colorForSettingsIcons
            tertiaryContainer = colorResource(R.color.black), // playlistBoxStrokeColor был раньше белый colorPrimaryVariant сейчас использую как colorPrimaryVariant
            inversePrimary = colorResource(R.color.text_gray), // colorForTitlesNameAudioPlayer
            primaryContainer = colorResource(R.color.white) // colorForTrackName
        )
    } else {
        lightColorScheme(
            // Стандартные цвета
            primary = colorResource(R.color.blue), // colorPrimary
            onPrimary = colorResource(R.color.white), // colorOnPrimary
            secondary = colorResource(R.color.white), // colorSecondary
            onSecondary = colorResource(R.color.black), // colorOnSecondary
            background = colorResource(R.color.white), // playerBackground / основной фон
            onBackground = colorResource(R.color.black), // читаемый текст на фоне
            surface = colorResource(R.color.white), // основной фон карточек / контейнеров
            onSurface = colorResource(R.color.black), // текст на surface
            // свободные поля для кастомных цветов
            surfaceVariant = colorResource(R.color.light_gray), // colorForMaterialCardView
            tertiary = colorResource(R.color.text_gray), // colorForSettingsIcons
            tertiaryContainer = colorResource(R.color.text_gray), // playlistBoxStrokeColor сейчас использую как colorPrimaryVariant
            inversePrimary = colorResource(R.color.white), // colorForTitlesNameAudioPlayer
            primaryContainer = colorResource(R.color.black_2) // colorForTrackName

        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )

}