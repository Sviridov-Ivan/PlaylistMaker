package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.viewmodels.PlaylistsFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    // Подключаем ViewModel через Koin
    private val viewModel: PlaylistsFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LiveData (логика появится позже)
        viewModel.observePlaceholderState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.None -> {
                    // ничего не показываем
                    binding.placeholderContainerPlaylists.visibility = View.GONE
                }
                is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.Empty -> {
                    // пустой плейсхолдер
                    binding.placeholderContainerPlaylists.visibility = View.VISIBLE
                    binding.placeholderTextPlaylists.text = getString(R.string.playlists_is_empty)
                }
                is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.Error -> {
                    // ошибка (пока тоже, что и пустой)
                    binding.placeholderContainerPlaylists.visibility = View.VISIBLE
                    binding.placeholderTextPlaylists.text = getString(R.string.playlists_is_empty)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}