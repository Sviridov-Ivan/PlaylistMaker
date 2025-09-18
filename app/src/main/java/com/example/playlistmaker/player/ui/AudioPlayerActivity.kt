package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.example.playlistmaker.player.domain.model.PlayerState
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.IntentKeys
import com.example.playlistmaker.util.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding //инициализация переменной для использования binding

    //работа с потоком
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val viewModel: AudioPlayerViewModel by viewModels { //создаем переменную для ViewModel
        AudioPlayerViewModel.getFactory(Creator.provideAudioPlayerInteractor())
    }

    // Runnable для обновления времени - экземпляр для внедрения в основной поток
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            viewModel.updateTime()
            handler.postDelayed(this, DELAY_MILLIS) // повтор каждые 300 мс
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // поддержка EdgeToEdge режима
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = intent.getParcelableExtra<Track>(IntentKeys.EXTRA_TRACK)
        track?.let { bindTrack(it) }

        // подписка на State
        viewModel.observePlayerState().observe(this) { state ->
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
        viewModel.observeCurrentTime().observe(this) { time ->
            binding.sourceTrackTimeMillsPlayer.text = time
        }

        // подписка на Toast
        viewModel.observeToastMessage().observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonPlay.setOnClickListener {
            if (track?.previewUrl.isNullOrEmpty()) {
                Toast.makeText(this, "Отрывок отсутствует для этого трека", Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.playbackControl()
            }
        }

        // закрытие активити
        binding.arrowBackToMainPlayer.setOnClickListener {
            finish()
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
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable) // остановка таймера
        viewModel.release() // вывод плейера из подготовки
    }

    companion object {
        private const val DELAY_MILLIS = 300L
    }
}