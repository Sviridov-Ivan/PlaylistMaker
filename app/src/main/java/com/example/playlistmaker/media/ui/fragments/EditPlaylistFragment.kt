package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.viewmodels.EditPlaylistViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment : NewPlaylistFragment() {

    companion object {
        const val ARG_PLAYLIST_ID = "playlist_id"
    }

    override val viewModel: EditPlaylistViewModel by viewModel {
        parametersOf(requireArguments().getLong(ARG_PLAYLIST_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel._uiState.collectLatest { state ->

                // имя
                val currentName = binding.inputEditTextNamePlaylist.text.toString()
                if (currentName != state.name) {
                    binding.inputEditTextNamePlaylist.setText(state.name)
                }

                // описание
                val currentDescription = binding.inputEditTextDescriptionPlaylist.text.toString()
                if (currentDescription != state.description) {
                    binding.inputEditTextDescriptionPlaylist.setText(state.description)
                }

                // обложка
                state.coverUri?.let { binding.sourcePlaylistImage.setImageURI(it) }
            }
        }

        // замена текста в заголовке и кнопке
        binding.nameNewPlaylist.text = getString(R.string.title_edit_playlist_fragment)
        binding.buttonAddPlaylist.text = getString(R.string.button_save_edit_playlist_fragment)

        // обработка нажатие иконки НАЗАД - просто закрытие, без диалога
        binding.arrowBackToMedia.setOnClickListener {
            findNavController().popBackStack()
        }

        // добавление слушателя для обработки нажатия на кнопку Back, проверка shouldShowExitDialog() и реализация диалога на закрытие
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                        findNavController().popBackStack()
                    }
            }
        )

        // кнопка СОХРАНИТЬ
        binding.buttonAddPlaylist.setOnClickListener {
            viewModel.createPlaylist() // в EditVM = updatePlaylist()
        }
    }
}