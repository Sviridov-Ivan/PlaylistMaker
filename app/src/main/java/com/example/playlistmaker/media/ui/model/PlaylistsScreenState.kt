package com.example.playlistmaker.media.ui.model

import androidx.compose.runtime.Immutable
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// класс описывающий состояние экрана Плейлистов
@Immutable
data class PlaylistsScreenState(
    val playlists: ImmutableList<Playlist> = persistentListOf(), // persistentListOf() для определения начального состояния
    val isPlaceholderVisible: Boolean = true
)