package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.PlaylistsMediaAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.ui.viewmodels.PlaylistsFragmentViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!


    private val viewModel: PlaylistsFragmentViewModel by viewModel() // подключаем ViewModel через Koin

    private val adapter = PlaylistsMediaAdapter() // переменная для адаптера

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewMedia.layoutManager = GridLayoutManager(requireContext(), /*Количество столбцов*/ 2)

        binding.recyclerViewMedia.adapter = adapter

        // переход на экран PlaylistFragment с передачей аргумента ID плейлиста (также прописан в Навигации фрагмента)  // Навигация через родительский MediaFragment (где определён action)
        adapter.setOnItemClickListener { playlist ->
            val bundle = Bundle().apply {
                putLong("playlistId", playlist.id)
            }

            NavHostFragment.findNavController(requireParentFragment())
                .navigate(
                    R.id.action_mediaFragment_to_playlistFragment,
                    bundle
                )
        }

        // переход на экран AddPlaylistFragment  // Навигация через родительский MediaFragment (где определён action)
        binding.newPlaylistButton.setOnClickListener {
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.action_mediaFragment_to_addPlaylistFragment)
        }

        observePlaylists() // функция для плейлистов
        observePlaceholder() // функция для плейсхолдеров
        viewModel.showPlaylists() // первичная загрузка данных (возможно лучше сделать через Старт (смотри ниже)
    }

    private fun observePlaylists() { // подписка на отображение адаптера с плейлистами
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { playlists ->
                    // обновление адаптера
                    adapter.updatePlaylists(playlists) //!!!
                }
            }
        }
    }

    private fun observePlaceholder() { // подписка на отображение плейсхолдера
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.placeholderState.collect { state ->
                    when (state) {
                        is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.Empty -> {
                            binding.placeholderContainerPlaylists.isVisible = true
                            binding.recyclerViewMedia.isVisible = false
                        }
                        is PlaylistsFragmentViewModel.PlaceholderPlaylistsState.None -> {
                            binding.placeholderContainerPlaylists.isVisible = false
                            binding.recyclerViewMedia.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.showPlaylists()
//    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}