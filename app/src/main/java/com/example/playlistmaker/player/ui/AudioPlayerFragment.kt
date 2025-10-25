package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue


class AudioPlayerFragment : Fragment() {

    companion object {
        private const val DELAY_MILLIS = 300L

    }
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    //работа с потоком
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    //ViewModel внедряется через Koin
    private val viewModel: AudioPlayerViewModel by viewModel()

    // Runnable для обновления времени - экземпляр для внедрения в основной поток
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            viewModel.updateTime()
            handler.postDelayed(this, DELAY_MILLIS) // повтор каждые 300 мс
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        val track = arguments?.getParcelable<Track>("track")
        track?.let { bindTrack(it) }


        // подписка на State
        viewModel.observePlayerState().observe(viewLifecycleOwner) { state ->
            when (state) {
                PlayerState.PLAYING -> {
                    binding.buttonPlay.setImageResource(R.drawable.button_pause)
                    handler.post(updateTimeRunnable)
                }

                PlayerState.PAUSED, PlayerState.PREPARED -> {
                    binding.buttonPlay.setImageResource(R.drawable.button_play)
                    handler.removeCallbacks(updateTimeRunnable)
                }

                else -> {}
            }
        }

        // подписка на Time
        viewModel.observeCurrentTime().observe(viewLifecycleOwner) { time ->
            binding.sourceTrackTimeMillsPlayer.text = time
        }

        // подписка на Toast
        viewModel.observeToastMessage().observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonPlay.setOnClickListener {
            if (track?.previewUrl.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Отрывок отсутствует для этого трека", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.playbackControl()
            }
        }

        // закрытие активити
        binding.arrowBackToMainPlayer.setOnClickListener {
            //parentFragmentManager.popBackStack() // не работает! крашится с ошибкой невозможности перехода на фрагмент повторно
            findNavController().navigateUp()
        }

        // подготовка плейера
        track?.previewUrl?.let {
            viewModel.prepare(it)
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

    // Если пользователь НЕ на экране плейера (свернул активити через кнопку Home или запускает другое приложение), то проигрывание на ПАУЗУ
    override fun onPause() {
        super.onPause()
        if (viewModel.playerStateLiveData.value == PlayerState.PLAYING) {
            viewModel.playbackControl() // поставить на паузу
        }
        handler.removeCallbacks(updateTimeRunnable) // остановка обновления проигрывания

    }

    // Если пользователь закрыл активити и медиаплеер и его возможности больше не нужны чтобы освободить память и ресурсы процессора, выделенные системой при подготовке медиаплеера
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateTimeRunnable) // остановка таймера
        viewModel.release() // вывод плейера из подготовки
        _binding = null
    }
}