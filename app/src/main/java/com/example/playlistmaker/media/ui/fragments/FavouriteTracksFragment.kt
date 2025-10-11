package com.example.playlistmaker.media.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.playlistmaker.media.ui.viewmodels.FavouriteTracksFragmentViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment : Fragment() {

    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!

    // Подключаем ViewModel через Koin
    private val viewModel: FavouriteTracksFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // LiveData (логика появится позже)
        viewModel.observePlaceholderState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.None -> {
                    // ничего не показываем
                    binding.placeholderContainerFavouriteTracks.visibility = View.GONE
                }
                is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.Empty -> {
                    // пустой плейсхолдер
                    binding.placeholderContainerFavouriteTracks.visibility = View.VISIBLE
                    binding.placeholderTextFavouriteTracks.text = getString(R.string.media_is_empty)
                }
                is FavouriteTracksFragmentViewModel.PlaceholderFavouriteTracksState.Error -> {
                    // ошибка (пока тоже, что и пустой)
                    binding.placeholderContainerFavouriteTracks.visibility = View.VISIBLE
                    binding.placeholderTextFavouriteTracks.text = getString(R.string.media_is_empty)
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