package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.PlaylistsScreen
import com.example.playlistmaker.media.ui.viewmodels.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setContent {
                PlaylistMakerTheme { // обернут, для использования моей цветовой гаммы

                    // запуск функции асинхронно во избежании множественных вызовов
                    LaunchedEffect(Unit) {
                        viewModel.showPlaylists()
                    }

                    val playlists by viewModel.playlists.collectAsState(initial = emptyList()) // плейлисты

                    val placeholderState by viewModel.placeholderState.collectAsState( // плейсхолдер
                        initial = PlaylistsFragmentViewModel.PlaceholderPlaylistsState.Empty
                    )

                    val isPlaceholderVisible =
                        placeholderState is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.Empty

                    // компоуз функция с версткой
                    PlaylistsScreen(

                        playlists = playlists,

                        isPlaceholderVisible = isPlaceholderVisible,

                        onPlaylistClick = { playlist ->

                            val bundle = Bundle().apply {
                                putLong("playlistId", playlist.id)
                            }

                            NavHostFragment.findNavController(requireParentFragment())
                                .navigate(
                                    R.id.action_mediaFragment_to_playlistFragment,
                                    bundle
                                )
                        },

                        onCreatePlaylistClick = {

                            NavHostFragment.findNavController(requireParentFragment())
                                .navigate(R.id.action_mediaFragment_to_addPlaylistFragment)

                        }
                    )
                }
            }
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment = PlaylistsFragment()
    }
}