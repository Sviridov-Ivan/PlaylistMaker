package com.example.playlistmaker.media.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.media.domain.model.NewPlaylist
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class NewPlaylistViewModel( // open так как наследуется в EditPlaylistViewModel
    val interactor: PlaylistInteractor // not private так как переиспользуется в EditPlaylistViewModel
) : ViewModel() {

    data class UiState(
        val name: String = "",
        val description: String = "",
        val coverUri: Uri? = null,
        val artworkPath: String? = null, //!!!!!!
        val isCreateButtonEnabled: Boolean = false
    )

    val _uiState = MutableStateFlow(UiState()) // убрал private, так как используется EditPlaylistViewModel
    val uiState: StateFlow<UiState> = _uiState

    // Одноразовые события для фрагмента
    val _events = MutableSharedFlow<Event>() // not private так как переиспользуется в EditPlaylistViewModel
    val events = _events.asSharedFlow()

    sealed class Event {
        object OpenPhotoPicker : Event()
        data class ShowToast(val message: String) : Event()
        object CloseScreen : Event()
    }

    fun onNameChanged(newValue: String) {
        _uiState.update {
            it.copy(
                name = newValue,
                isCreateButtonEnabled = newValue.isNotBlank()
            )
        }
    }

    fun onDescriptionChanged(newValue: String) {
        _uiState.update { it.copy(description = newValue) }
    }

    //!!!!!!
    fun onCoverSelected(uri: Uri, savedPath: String) {
        _uiState.value = _uiState.value.copy(
            coverUri = uri,
            artworkPath = savedPath
        )
    }

    fun onCoverClick() {
        viewModelScope.launch {
            _events.emit(Event.OpenPhotoPicker)
        }
    }

    fun shouldShowExitDialog(): Boolean {
        val state = _uiState.value
        return state.name.isNotBlank() ||
                state.description.isNotBlank() ||
                state.coverUri != null
    }

   open fun createPlaylist() { // open так как переиспользуется в EditPlaylistViewModel
        val state = _uiState.value
        if (state.name.isBlank()) return

        viewModelScope.launch {
            val newPlaylist = NewPlaylist(
                name = state.name,
                description = state.description,
                artworkPath = state.coverUri?.toString()
            )

            interactor.addPlaylist(newPlaylist)

            _events.emit(Event.ShowToast("Плейлист «${state.name}» создан"))
            _events.emit(Event.CloseScreen)
        }
    }
}