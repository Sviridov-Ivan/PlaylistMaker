package com.example.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.PlaylistsBottomSheetAdapter
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.player.service.PlayerService
import com.example.playlistmaker.player.service.PlayerServiceController
import com.example.playlistmaker.player.ui.customview.PlaybackButtonState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.NetworkReceiver
import com.example.playlistmaker.util.dpToPx
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue
private const val OVERLAY_MAX_ALPHA = 0.7f // максимальное затемнение при BottomSheet


class AudioPlayerFragment : Fragment() {

    private val networkReceiver = NetworkReceiver() // объект класса NetworkReceiver для работы с отслеживанием наличия доступа к сети Интернет
    private var isReceiverRegistered = false // проверка зарегистрирован ли ресивер, если onPause() сработает раньше

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!


// передаем track в ViewModel через Koin
    private val track: Track by lazy {
        requireArguments().getParcelable<Track>("track")
            ?: error("Track is missing in AudioPlayerFragment")
    }
    private val viewModel: AudioPlayerViewModel by viewModel { parametersOf(track) }

    private val adapter = PlaylistsBottomSheetAdapter() // переменная для адаптера BottomSheet

    // связь с Сервисом
    private var serviceBound = false
    private var audioPlayerControl: PlayerServiceController? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as PlayerService.PlayerServiceBinder
            audioPlayerControl = serviceBinder.getService()

            // передаем сервис во ViewModel
            audioPlayerControl?.let {
                viewModel.setAudioPlayerControl(it)
            }

            serviceBound = true

            // prepare сразу после подключения
            viewModel.prepare()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            audioPlayerControl = null
            viewModel.removeAudioPlayerControl()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.player)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // настройка RecyclerView для альбомов в BottomSheet
        binding.recyclerViewBottomSheet.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewBottomSheet.adapter = adapter

        // переход на экран AddPlaylistFragment
        binding.newPlayListButtonBottomSheet.setOnClickListener {
            findNavController().navigate(
                R.id.action_audioPlayerFragment_to_addPlaylistFragment)
        }

        // получаем track из аргументов фрагмента
        bindTrack(track)
        // подписка на State, но уже с использованием состояния кастомного элемента PlaybackButtonState
        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            binding.buttonPlay.setState(
                when (state) {
                    PlayerState.PLAYING -> PlaybackButtonState.PAUSE
                    PlayerState.PAUSED, PlayerState.PREPARED -> PlaybackButtonState.PLAY
                    else -> PlaybackButtonState.PLAY
                }
            )
        }

        // подписка на Time
        viewModel.observeCurrentTime().observe(viewLifecycleOwner) { time ->
            binding.sourceTrackTimeMillsPlayer.text = time
        }

        // подписка на Toast с AudioPlayerViewModel.ToastEvent
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collect { event ->
                    event?.let {
                        val message = when (it) {
                            is AudioPlayerViewModel.ToastEvent.Added -> getString(
                                R.string.track_added_to_playlist,
                                it.playlistName
                            )

                            is AudioPlayerViewModel.ToastEvent.AlreadyExists -> getString(
                                R.string.track_already_in_playlist,
                                it.playlistName
                            )
                        }
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearToastMessage()
                    }
                }
            }
        }

        // отображение иконки "избранные" в соответствии с состоянием трека
        viewModel.observeIsFavourite().observe(viewLifecycleOwner) { isFavourite ->
            if (isFavourite) {
                binding.buttonLike.setImageResource(R.drawable.button_liked)
            } else {
                binding.buttonLike.setImageResource(R.drawable.button_like)
            }
        }

        // обработка нажатия уже на кастомный элемент PlaybackButtonView
        binding.buttonPlay.onClick = {
            if (track.previewUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_track), Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.playbackControl()
            }
        }

        // возврат на фрагмент Поиск
        binding.arrowBackToMainPlayer.setOnClickListener {
            //parentFragmentManager.popBackStack() // не работает! крашится с ошибкой невозможности перехода на фрагмент повторно
            findNavController().navigateUp()
        }

        // вызов функции для работы с избранными треками из viewModel
        binding.buttonLike.setOnClickListener {
            viewModel.onFavoriteClicked()
        }

        // работа с BottomSheet
        val bottomSheetContainer = binding.playlistsBottomSheet

        //  BottomSheetBehavior.from() — вспомогательная функция, позволяющая получить объект BottomSheetBehavior, связанный с контейнером BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        // обработка нажатия на кнопку Добавить трек в Альбом
        binding.buttonAddTopPl.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // обработка нажатия на оверлей для скрытия BottomSheet (дополнительно к свайпу)
        binding.overlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        binding.overlay.alpha = 0f
                    }
                    else -> {
                        binding.overlay.isVisible = true
                        binding.overlay.alpha = OVERLAY_MAX_ALPHA
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset <= 0f) return

                binding.overlay.alpha = slideOffset * OVERLAY_MAX_ALPHA
            }
        })

        viewModel.showPlaylistsBottomSheet() // первичная загрузка данных о плейлистах(возможно лучше сделать через Старт, смотри ниже)
        observePlaylists() // отображение списка с плейлистами в BottomSheet

        // обработчик клика по плейлисту в BottomSheet
        adapter.setOnItemClickListener { playlist ->
            viewModel.onPlaylistClicked(playlist)
        }

        // подписка на плейлисты v BottomSheet
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { playlists ->
                    adapter.updatePlaylists(playlists)
                }
            }
        }

        // подписка на событие для работы с BottomSheet
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvents.collect { event ->
                    when (event) {
                        is AudioPlayerViewModel.UiEvent.HideBottomSheet -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                    }
                }
            }
        }
    }

    // подписка на отображение адаптера с плейлистами в BottomSheet
    private fun observePlaylists() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playlists.collect { playlists ->
                    // обновление адаптера
                    adapter.updatePlaylists(playlists) //!!!
                }
            }
        }
    }

    private fun bindTrack(track: Track) {
        binding.apply {
            track.apply {
                sourceTrackNamePlayer.text = trackName
                sourceArtistNamePlayer.text = artistName
                sourceTrackTimePlayer.text =
                    com.example.playlistmaker.util.formatDuration(trackTimeMillis) // используем отформатированное значение времени функция в файле TimeUtils
                sourceCollectionNamePlayer.text = collectionName.orEmpty()
                sourceReleaseDatePlayer.text = releaseDate?.take(4)
                    .orEmpty() // из json "releaseDate": "2010-06-21T07:00:00Z" берем только первые 4 значения
                sourcePrimaryGenreNamePlayer.text = primaryGenreName.orEmpty()
                sourceCountryPlayer.text = country.orEmpty()

                val radiusPx = dpToPx(
                    8f,
                    root.context
                ) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide для элемента artworkView

                Glide.with(root) // обработка картинки из Глайд
                    .load(
                        artworkUrl100.replaceAfterLast(
                            '/',
                            "512x512bb.jpg"
                        )
                    ) // увеличение разрешения каритинки до 512x512. По сути это код для замены в адресе картинки последнего сегмента после /....jpg/100x100bb.jpg. (это по умолчанию)
                    //.centerCrop()
                    .transform(RoundedCorners(radiusPx)) // скугления углов картинки до 8dp(px Figma)
                    .placeholder(R.drawable.placeholder)
                    .into(sourceTrackImagePlayer)
            }
        }
    }

    // проверка наличия разрешения на отображение foreground сервиса

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_permission_for_foreground_service),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    // Если пользователь НЕ на экране плейера (свернул активити через кнопку Home или запускает другое приложение), то проигрывание на ПАУЗУ
    override fun onPause() {
        super.onPause()
        // отмена регистрации ресивера, чтобы избежать утечек памяти, при выгрузке из фрагмента
        if (isReceiverRegistered) { // проверка на регистрацию ресивера
            requireContext().unregisterReceiver(networkReceiver)
            isReceiverRegistered = false
        }
        viewModel.onUiHidden()
    }

    override fun onResume() {
        super.onResume()
        // регистрация в onResume и съем в onPause, чтобы receiver работал только когда экран активен
        if (!isReceiverRegistered) { // проверка на регистрацию ресивера
            val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

            ContextCompat.registerReceiver(
                requireContext(),
                networkReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            isReceiverRegistered = true
        }
        viewModel.onUiVisible()
    }

    override fun onStart() {
        super.onStart()
        // BIND привязка Сервиса PlayService
        val intent = Intent(requireContext(), PlayerService::class.java)

        // сервис запущен
        //ContextCompat.startForegroundService(requireContext(), intent)

        // привязка к сервису для управления
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)

        viewModel.onUiVisible() // остановка сервиса foreground уведомления

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (serviceBound) {

            if (viewModel.observePlayerState().value == PlayerState.PLAYING) {

                // запускаем сервис
                val intent = Intent(requireContext(), PlayerService::class.java)
                requireContext().startService(intent)

                viewModel.onUiHidden() // запуск сервиса foreground уведомления
            }

            requireContext().unbindService(connection)
            serviceBound = false
        }
    }

    // Если пользователь закрыл активити и медиаплеер и его возможности больше не нужны чтобы освободить память и ресурсы процессора, выделенные системой при подготовке медиаплеера
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.release() // вывод плейера из подготовки
        _binding = null
    }
}