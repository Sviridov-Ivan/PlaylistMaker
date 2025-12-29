package com.example.playlistmaker.media.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.TracksAdapter
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.ui.viewmodels.PlaylistFragmentViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.DebounceConfig.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.util.dpToPx
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
private const val OVERLAY_MAX_ALPHA = 0.7f // максимальное затемнение при BottomSheet

class PlaylistFragment : Fragment() {

    private val externalNavigator by lazy { ExternalNavigatorImpl(requireActivity()) }
    private val viewModel: PlaylistFragmentViewModel by viewModel() // подключаем ViewModel через Koin

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val adapter = TracksAdapter() // переменная для адаптера трека (для отображения в BottomSheet, перехода, диалога

    private lateinit var onTrackClickDebounce: (Track) -> Unit // объявление функции для работы с Debounce.kt для задержки и исключения многократного нажатия при переходе на Аудиоплейер

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.playlist_layout_basement)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom) // тор=0, чтобы хедер находилась под systemBars сверху
            insets
        }

        // компенсация сверху для стрелки назад из-за нахождения хедера под статус баром сверху
        ViewCompat.setOnApplyWindowInsetsListener(
            requireView().findViewById(R.id.arrow_back_to_playlists)
        ) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = systemBars.top + resources.getDimensionPixelSize(R.dimen.margin_arrow_player) }
            insets
        }

        // настройка RecyclerViewPlaylistBottomSheet
        binding.recyclerViewPlaylistBottomSheet.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) // вызываем адаптер для LinearLayoutManager (составляющий элемент RecyclerView помимо адаптера и вьюхолдера)
        binding.recyclerViewPlaylistBottomSheet.adapter = adapter
        // для реализации прокрутки треков именно recyclerView, а не растягиванием BottomSheet
        binding.recyclerViewPlaylistBottomSheet.isNestedScrollingEnabled = true

        // инициализация переменной для работы с корутинами (работа с потоком) с использованием функции из файла Debounce.kt
        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->  // реализация дебонса - задержки на открытие активити на CLICK_DEBOUNCE_DELAY при нажатии

            // парсинг track
            val bundle = Bundle().apply {
                putParcelable("track", track)
            }
            // переход на экрн AudioPlayer
            findNavController().navigate(
                R.id.action_playlistFragment_to_audioPlayerFragment, bundle
            )
        }
        // oбработка КОРОТКОГО клика по элементу списка RecyclerViewPlaylistBottomSheet
        adapter.setOnItemClickListener { track ->
            onTrackClickDebounce(track)
        }

        // возврат на материнский фрагмент Медиатека
        binding.arrowBackToPlaylists.setOnClickListener {
            findNavController().navigateUp()
        }

        // инициализация BottomSheet для отображения ТРЕКОВ ДО подписки на плейлист
        // работа с BottomSheet для отображения ТРЕКОВ плейлиста
        val bottomSheetContainer = binding.playlistBottomSheetTracks

        //  BottomSheetBehavior.from() — вспомогательная функция, позволяющая получить объект BottomSheetBehavior, связанный с контейнером BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
            isDraggable = false   // запрет тянуть BottomSheet вверх
        }

        // ограничение высоты BottomSheet для треков после измерения
        bottomSheetContainer.doOnLayout {

            val screenHeight = resources.displayMetrics.heightPixels

            // фиксированная высота — 30% экрана (можно менять)
            val maxHeight = (screenHeight * 0.32f).toInt()

            bottomSheetContainer.layoutParams.height = maxHeight
            bottomSheetContainer.requestLayout()

            bottomSheetBehavior.peekHeight = maxHeight
            bottomSheetBehavior.skipCollapsed = false
            bottomSheetBehavior.isFitToContents = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // считывание аргумента, переданного во время навигации по ключу
        val playlistId = requireArguments().getLong("playlistId")
        viewModel.loadPlaylist(playlistId) // загрузка данных через вью по id

        // получения данных плейлиста (подписка), привязанная к жизненному циклу фрагмента
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlist.collect { playlist ->
                playlist ?: return@collect // из-за начального значения StateFlow null

                // основной экран
                binding.playlistName.text = playlist.name // имя
                binding.playlistDescription.text = playlist.description // описание

                // количество треков в альбоме + работа с отображением во множественном числе
                val count = playlist.trackCount
                binding.playlistTracksQuantity.text = resources.getQuantityString(
                        R.plurals.playlist_tracks_count,
                        count,
                        count)

                if (playlist.artworkPath != null) { // загрузка обложки из альбома, если есть
                    Glide.with(binding.root)
                        .load(Uri.parse(playlist.artworkPath))
                        .placeholder(R.drawable.placeholder_album)
                        .centerCrop()
                        .into(binding.playlistCoverImageInPlaylist)
                } else {
                    binding.playlistCoverImageInPlaylist.setImageResource(R.drawable.placeholder_album)
                }

                // menu bottomSheet
                binding.playlistNameMenuBottomSheet.text = playlist.name
                // количество треков в альбоме + работа с отображением во множественном числе
                binding.playlistTracksQuantityMenuBottomSheet.text = resources.getQuantityString(
                    R.plurals.playlist_tracks_count,
                    count,
                    count)

                val radiusPx = dpToPx(
                    2f,
                    requireContext()
                ) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide

                if (playlist.artworkPath != null) { // загрузка обложки из альбома, если есть
                    Glide.with(binding.root)
                        .load(Uri.parse(playlist.artworkPath))
                        .placeholder(R.drawable.placeholder_album)
                        .centerCrop()
                        .transform(RoundedCorners(radiusPx))
                        .into(binding.playlistCoverImageInPlaylistMenuBottomSheet)
                } else {
                    binding.playlistCoverImageInPlaylistMenuBottomSheet.setImageResource(R.drawable.placeholder_album)
                }

                // получение треков в конкретном плейлисте для отображения в BottomSheet с треками
                if (playlist.trackIds.isNotEmpty()) {
                    viewModel.showPlaylistsBottomSheet(playlist.trackIds)
                }
            }
        }

        // получение суммы времени треков из вью
        viewLifecycleOwner.lifecycleScope.launch { // получение суммы времени треков из вью
            viewModel.totalDuration.collect { minutes ->
                binding.playlistAllMinutes.text =
                    resources.getQuantityString(
                        R.plurals.playlist_minutes_count,
                        minutes,
                        minutes
                    )
            }
        }

        // oбработка ДЛИННОГО клика по элементу списка RecyclerViewPlaylistBottomSheet для вызова ДИАЛОГО об удалении трека из плейлиста
        adapter.setOnItemLongClickListener { track ->
            showDeleteTrackDialog(track)
        }

        viewModel.loadPlaylist(playlistId) // первичная загрузка данных о треках в BottomSheet (возможно лучше сделать через Старт, смотри ниже)


        // подписка на треки v BottomSheet из ВьюМодели и подписка на отображение треков и плейсхолдера, если треков нет (в одном launch, меньше ресурсов)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.tracks.collect { tracks ->
                        adapter.updateTracks(tracks)
                    }
                }

                launch {
                    viewModel.placeholderTracksState.collect { state ->
                        when (state) {
                            is PlaylistFragmentViewModel.PlaceholderTracksStateInPlaylist.Empty -> {
                                binding.placeholderContainerTracksInPlaylist.isVisible = true
                                binding.recyclerViewPlaylistBottomSheet.isVisible = false

                                binding.placeholderContainerTracksInPlaylist.post {
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                }
                            }
                            is PlaylistFragmentViewModel.PlaceholderTracksStateInPlaylist.NotEmpty -> {
                                binding.placeholderContainerTracksInPlaylist.isVisible = false
                                binding.recyclerViewPlaylistBottomSheet.isVisible = true

                                binding.recyclerViewPlaylistBottomSheet.post {
                                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                                }
                            }
                        }
                    }
                }
            }
        }

        // работа с BottomSheet при нажатии на иконку МЕНЮ
        val bottomSheetContainerMenu = binding.playlistBottomSheetMenu

        //  BottomSheetBehavior.from() — вспомогательная функция, позволяющая получить объект BottomSheetBehavior, связанный с контейнером BottomSheet
        val bottomSheetBehaviorMenu = BottomSheetBehavior.from(bottomSheetContainerMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        // обработка нажатия на иконку МЕНЮ
        binding.playlistMenu.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // обработка нажатия на оверлей для скрытия BottomSheet Меню (дополнительно к свайпу)
        binding.overlayPlaylistForMenuBottomSheet.setOnClickListener {
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehaviorMenu.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlayPlaylistForMenuBottomSheet.isVisible = false
                        binding.overlayPlaylistForMenuBottomSheet.alpha = 0f
                    }
                    else -> {
                        binding.overlayPlaylistForMenuBottomSheet.isVisible = true
                        binding.overlayPlaylistForMenuBottomSheet.alpha = OVERLAY_MAX_ALPHA
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= 0f) return

                binding.overlayPlaylistForMenuBottomSheet.alpha = slideOffset * OVERLAY_MAX_ALPHA
            }
        })

        // подписка на состояние функции ПОДЕЛИТЬСЯ
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.shareState.collect { state ->
                when(state) {
                    is PlaylistFragmentViewModel.PlaylistShareState.Idle -> Unit
                    is PlaylistFragmentViewModel.PlaylistShareState.ShowMessage -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetShareState() // чтобы тост не повторялся при повороте
                    }
                    is PlaylistFragmentViewModel.PlaylistShareState.ShareText -> {
                        externalNavigator.shareText(requireActivity(), state.text)//viewModel.handleShareText(state.text)
                        viewModel.resetShareState()
                    }
                }
            }
        }

        // обработка нажатия на иконку ПОДЕЛИТЬСЯ на основном экране
        binding.playlistShare.setOnClickListener {
            viewModel.sharePlaylist()
        }

        //обработка нажатия на линеар ПОДЕЛИТЬСЯ в BottomSheet Меню
        binding.playlistShareMenuBottomSheet.setOnClickListener {
            viewModel.sharePlaylist()
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN // скрытие BottomSheet Меню при нажатии
        }

        // подписка на состояние удаления плейлиста
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteState.collect { state ->
                when (state) {

                    is PlaylistFragmentViewModel.PlaylistDeleteState.ShowConfirmDialog -> {
                        showConfirmDeleteDialog(state.playlistName)
                    }

                    PlaylistFragmentViewModel.PlaylistDeleteState.Deleted -> {
                        Toast.makeText(requireContext(),getString(R.string.toast_playlist_deleted_playlist_fragment),Toast.LENGTH_SHORT).show()

                        findNavController().navigateUp()
                    }

                    is PlaylistFragmentViewModel.PlaylistDeleteState.Error -> { Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        // обработка нажатия на элемент УДАЛИТЬ в BottomSheet Меню и вызов функции диалога во вью
        binding.playlistDeleteMenuBottomSheet.setOnClickListener {
            viewModel.onDeletePlaylistClicked()
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN // скрытие BottomSheet Меню при нажатии
        }

        // переход на наследуемый фрагмент для редактирования Плейлиста
        binding.playlistEditInfoMenuBottomSheet.setOnClickListener {
            val currentPlaylist = viewModel.playlist.value
            if (currentPlaylist != null) {
                findNavController().navigate(
                    R.id.action_playlistFragment_to_editPlaylistFragment,
                    bundleOf("playlist_id" to currentPlaylist.id)
                )
            } else {
                Toast.makeText(requireContext(), getString(R.string.toast_data_not_loaded_playlist_fragment),
                    Toast.LENGTH_SHORT).show()
            }
            bottomSheetBehaviorMenu.state = BottomSheetBehavior.STATE_HIDDEN // скрытие BottomSheet Меню при нажатии
        }
    }

    // функция для отображения диалога при Долгом нажатии на элемент Ресайклера
    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_from_playlist_dialog_title))
            .setMessage("")
            .setNegativeButton(getString(R.string.delete_track_from_playlist_dialog_negative_button), null)
            .setPositiveButton(getString(R.string.delete_track_from_playlist_dialog_positive_button)) { dialog, which ->
                viewModel.deleteTrackFromPlaylist(track)
            }
            .show()
    }

    // функция отображения диалога при нажатии на элемент УДАЛИТЬ
    private fun showConfirmDeleteDialog(playlistName: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_dialog_title))
            .setMessage(getString(R.string.delete_playlist_dialog_message))
            .setNegativeButton(getString(R.string.delete_playlist_dialog_negative_button), null)
            .setPositiveButton(getString(R.string.delete_playlist_dialog_positive_button)) { dialog, which ->
                viewModel.confirmPlaylistDelete()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    //    override fun onStart() {
//        super.onStart()
//        viewModel.loadPlaylist(playlistId)
//    }


}
