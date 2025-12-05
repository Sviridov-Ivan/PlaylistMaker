package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.TracksAdapter
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.media.ui.viewmodels.FavouriteTracksFragmentViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.DebounceConfig.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {

    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    // создаем переменную для адаптера с пустым конструктором (там есть пометка)
    private val adapter = TracksAdapter()

    // Подключаем ViewModel через Koin
    private val viewModel: FavouriteTracksFragmentViewModel by viewModel()

    // объявление функции для работы с Debounce.kt для задержки и исключения многократного нажатия при переходе на Аудиоплейер
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // настройка RecyclerView
        // поправил использование Context
        binding.recyclerViewFav.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        ) // вызываем адаптер для LinearLayoutManager (составляющий элемент RecyclerView помимо адаптера и вьюхолдера)
        binding.recyclerViewFav.adapter = adapter

        // инициализация переменной для работы с корутинами (работа с потоком) с использованием функции из файла Debounce.kt
        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->  // реализация дебонса - задержки на открытие активити на CLICK_DEBOUNCE_DELAY при нажатии (спринт 14,20)

            // парсинг track
            val bundle = Bundle().apply {
                putParcelable("track", track)
            }

            // переход на экран AudioPlayer  // Навигация через родительский MediaFragment (где определён action)
            NavHostFragment.findNavController(requireParentFragment())
                .navigate(R.id.action_mediaFragment_to_audioPlayerFragment, bundle)
        }

        // oбработка клика по элементу списка RecyclerView и вызов функции для перехода на фрагмент Аудиоплейер
        adapter.setOnItemClickListener { track ->
            onTrackClickDebounce(track)
        }

        observeFavViewModel() // вызов функции для избранных треков
    }
    private fun observeFavViewModel() {
        // список избранных треков
        viewModel.observeFavourTracks().observe(viewLifecycleOwner) { favTracks ->
            adapter.updateTracks(favTracks)
        }

        // LiveData отображение заглушки и треков
        viewModel.observePlaceholderState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.Empty -> {
                    // пустой плейсхолдер
                    //binding.placeholderContainerFavouriteTracks.visibility = View.VISIBLE
                    binding.placeholderContainerFavouriteTracks.isVisible = true
                    //binding.recyclerViewFav.visibility = View.GONE
                    binding.recyclerViewFav.isVisible = false
                }
                is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.Favourites -> {
                    //binding.placeholderContainerFavouriteTracks.visibility = View.GONE
                    binding.placeholderContainerFavouriteTracks.isVisible = false
                    //binding.recyclerViewFav.visibility = View.VISIBLE
                    binding.recyclerViewFav.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FavouriteTracksFragment {
            return FavouriteTracksFragment() // возвращаем новый экземпляр фрагмента
        }
    }
}