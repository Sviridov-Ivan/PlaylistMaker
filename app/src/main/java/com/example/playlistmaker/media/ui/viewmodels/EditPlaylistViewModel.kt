package com.example.playlistmaker.media.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.launch
import java.io.File

class EditPlaylistViewModel(
    private val playlistId: Long, // так лучше прогужать по id
    interactor: PlaylistInteractor
) : NewPlaylistViewModel(interactor) {

    private lateinit var playlist: Playlist

    init {
        // загрука плейлиста и заполнение UI
        viewModelScope.launch {
            val loaded = interactor.getPlaylistById(playlistId)
            if (loaded != null) { // проверка возврата данных из БД, если есть данные, то далее
                playlist = loaded

                _uiState.value = UiState(
                    name = playlist.name,
                    description = playlist.description.orEmpty(),
                    coverUri = playlist.artworkPath?.let { path ->
                        if (path.startsWith("/")) { // проверка на путь для обложки
                            Uri.fromFile(File(path))
                        } else {
                            Uri.parse(path)
                        }
                    },
                    isCreateButtonEnabled = true
                )
            } else {
                // если плейлист не найден — экран закрывается
                _events.emit(Event.ShowToast("Плейлист не найден"))
                _events.emit(Event.CloseScreen)
            }
        }
    }

    override fun createPlaylist() {
        if (!::playlist.isInitialized) return // защита на случай, если данные еще не пришли

        val state = _uiState.value

        viewModelScope.launch {

            // если пользователь НЕ менял обложку — остается старую
            val artworkPath = state.coverUri?.toString() ?: playlist.artworkPath

            val updatedPlaylist = playlist.copy(
                name = state.name,
                description = state.description,
                artworkPath = artworkPath //state.coverUri?.toString()
            )

            try {
                interactor.updatePlaylist(updatedPlaylist)

                _events.emit(Event.ShowToast("Изменения сохранены"))
                _events.emit(Event.CloseScreen)

            } catch (e: Exception) {
                _events.emit(Event.ShowToast("Ошибка при сохранении изменений"))
            }
        }
    }
}
