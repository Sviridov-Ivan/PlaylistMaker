package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.PlaylistsScreenRoute

class PlaylistsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            // добавлена стратегия - Композиция уничтожается только когда уничтожается View фрагмента (onDestroyView), рекомендуется Гуглом при совместной работе Фрагментов и компоуз функций
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                PlaylistMakerTheme { // обернут, для использования моей цветовой гаммы

                    PlaylistsScreenRoute(
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