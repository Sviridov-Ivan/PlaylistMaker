package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.map
import androidx.navigation.fragment.NavHostFragment
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.FavouriteTracksScreen
import com.example.playlistmaker.media.ui.viewmodels.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.DebounceConfig.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.collections.immutable.toImmutableList

class FavouriteTracksFragment : Fragment() {


    // Подключаем ViewModel через Koin
    private val viewModel: FavouriteTracksFragmentViewModel by viewModel()

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

                PlaylistMakerTheme { // обернут для использования моей цветовой гаммы

                    val tracks by viewModel.observeFavourTracks().observeAsState(emptyList())
                    //val placeholderVisible by viewModel.observePlaceholderState().observeAsState(true)
                    // any to boolean
                    val placeholderVisible by viewModel.observePlaceholderState()
                        .map { state ->
                            state is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.Empty
                        }
                        .observeAsState(true)
                    val scope = rememberCoroutineScope()

                    val debouncedClick = remember {
                        debounce<Track>(CLICK_DEBOUNCE_DELAY, scope, false) { track ->
                            NavHostFragment.findNavController(requireParentFragment())
                                .navigate(
                                    R.id.action_mediaFragment_to_audioPlayerFragment,
                                    Bundle().apply { putParcelable("track", track) }
                                )
                        }
                    }

                    FavouriteTracksScreen(
                        tracks = tracks.toImmutableList(), // // немьютабельные коллекции для повышения стабильности композабл функций. В данном случае используется ImmutableList (чтобы сказать компилятору)
                        isPlaceholderVisible = placeholderVisible,
                        onTrackClick = { track -> debouncedClick(track) }
                    )
                }
            }
        }
    }
    companion object {
        fun newInstance(): FavouriteTracksFragment {
            return FavouriteTracksFragment() // возвращаем новый экземпляр фрагмента
        }
    }
}