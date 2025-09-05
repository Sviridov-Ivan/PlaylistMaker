package com.example.playlistmaker.presentation.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.presentation.util.IntentKeys
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.model.PlayerState
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.util.dpToPx
import com.example.playlistmaker.presentation.util.formatDuration
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private var previewUrlView: String? = null // переменная для доступа к previewUrl вне let{}
    private val interactor: AudioPlayerInteractor by lazy { Creator.provideAudioPlayerInteractor() }

    /*companion object { // текущее состояние медиаплеера и четыре константы для каждого из состояний
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }*/

    //private var playerState = STATE_DEFAULT // текущее состояние медиаплеера
    private lateinit var play: ImageButton
    /*private var mediaPlayer =
        MediaPlayer() */// инициализация класс медиаплейер для работы с его методами далее

    // переменные для работы с временем проигрывания
    private lateinit var timeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    // Runnable для обновления времени - экземпляр для внедрения в основной поток
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (interactor.getState() == PlayerState.PLAYING) {
                val currentTime = timeFormat.format(interactor.currentPosition())
                timeTextView.text = currentTime
                handler.postDelayed(this, 300) // повтор каждые 300 мс
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // поддержка EdgeToEdge режима
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val track = intent.getParcelableExtra<Track>(IntentKeys.EXTRA_TRACK)

        // переменные для связи активити и ХМЛ (сделал сверху вниз)

        val viewArrowBackToSearchActivity = findViewById<ImageView>(R.id.arrow_back_to_main_player)

        val artworkView = findViewById<ImageView>(R.id.source_track_image_player)
        val trackNameView = findViewById<TextView>(R.id.source_track_name_player)
        val artistNameView = findViewById<TextView>(R.id.source_artist_name_player)
        val trackTimeView = findViewById<TextView>(R.id.source_track_time_player)
        val albumView = findViewById<TextView>(R.id.source_collection_name_player)
        val releaseYearView = findViewById<TextView>(R.id.source_release_date_player)
        val genreView = findViewById<TextView>(R.id.source_primaryGenreName_player)
        val countryView = findViewById<TextView>(R.id.source_country_player)

        val radiusPx = dpToPx(8f,artworkView.context) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide для элемента artworkView


        // получение данных из @Parcelize дата класса Track
        track?.let {
            trackNameView.text = it.trackName
            artistNameView.text = it.artistName
            trackTimeView.text = formatDuration(it.trackTimeMillis) // используем отформатированное значение времени функция в файле TimeUtils
            //previewUrlView = it.previewUrl // !!!

            //если есть данные по альбому, тогда отображаем Вью, если нет данных то нет
            if (!it.collectionName.isNullOrEmpty()) {
                albumView.text = it.collectionName
                albumView.visibility = View.VISIBLE
            } else {
                albumView.visibility = View.GONE
            }

            //если есть году выхода по альбому, тогда отображаем Вью, если нет данных то нет
            if (!it.releaseDate.isNullOrEmpty()) {
                val year = it.releaseDate.take(4) // из json "releaseDate": "2010-06-21T07:00:00Z" берем только первые 4 значения
                releaseYearView.text = year
                releaseYearView.visibility = View.VISIBLE
            } else {
                releaseYearView.visibility = View.GONE
            }

            //если есть данные по жанру, тогда отображаем Вью, если нет данных то нет
            if (!it.primaryGenreName.isNullOrEmpty()) {
                genreView.text = it.primaryGenreName
                genreView.visibility = View.VISIBLE
            } else {
                genreView.visibility = View.GONE
            }

            //если есть данные по стране, тогда отображаем Вью, если нет данных то нет
            if (!it.country.isNullOrEmpty()) {
                countryView.text = it.country
                countryView.visibility = View.VISIBLE
            } else {
                countryView.visibility = View.GONE
            }

            Glide.with(this) // обработка картинки из Глайд
                .load(it.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")) // увеличение разрешения каритинки до 512x512. По сути это код для замены в адресе картинки последнего сегмента после /....jpg/100x100bb.jpg. (это по умолчанию)
                //.centerCrop()
                .transform(RoundedCorners(radiusPx)) // скугления углов картинки до 8dp(px Figma)
                .placeholder(R.drawable.placeholder)
                .into(artworkView)

            interactor.prepare(
                it.previewUrl ?: "", // источник воспроизведения перед вызовом метода подготовки
                onPrepared = { play.isEnabled = true }, // кнопка стала активной после подготовки
                onCompletion = {
                    play.setImageResource(R.drawable.button_play)
                    handler.removeCallbacks(updateTimeRunnable) // остановка таймера при завершении трека
                    timeTextView.text = getString(R.string.temporary_source_track_time_mills_player) // 00:00
                }
            )
        }

        // АудиоПлейер
        play = findViewById(R.id.button_play) // кнопка Плей

        //preparePlayer(previewUrlView) // размещается здесь, так как функцию подготовки нужно сделать только раз

        // Вызов функции выбора подходящего действия по нажатию на кнопку Плей
        /*play.setOnClickListener {
            playbackControl()
        }*/

        // Инициализация строки состояния проигрывания через текст вью
        timeTextView = findViewById(R.id.source_track_time_mills_player)
        timeTextView.text = getString(R.string.temporary_source_track_time_mills_player) //00:00

        play.setOnClickListener {
            if (track?.previewUrl.isNullOrEmpty()) {
                Toast.makeText(this, "Отрывок отсутствует для этого трека", Toast.LENGTH_SHORT)
                    .show()
            } else {
                interactor.playbackControl()
                when (interactor.getState()) {
                    PlayerState.PLAYING -> {
                        play.setImageResource(R.drawable.button_pause) // отображается иконка кнопки Pause
                        handler.post(updateTimeRunnable) // запуск обновления времени проигрывания
                    }

                    PlayerState.PAUSED, PlayerState.PREPARED -> {
                        play.setImageResource(R.drawable.button_play) // отображается иконка кнопки Play
                        handler.removeCallbacks(updateTimeRunnable) // остановка обновления проигрывания
                    }

                    else -> {}
                }
            }
        }

        // Реализация возврата назад на экран SearchActivity, путем завершения AudioPlayerActivity
        viewArrowBackToSearchActivity.setOnClickListener {
            finish()
        }
    }

    /*private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) { // проверка на наличие отрывка трека
            Toast.makeText(
                this@AudioPlayerActivity,
                "Отрывок отсутствует для этого трека",
                Toast.LENGTH_SHORT).show()
            return
        }
        mediaPlayer.setDataSource(previewUrl) // источник воспроизведения перед вызовом метода подготовки
        mediaPlayer.prepareAsync() // prepareAsync так как из Интернета, следовательно может быть дольше и используем НЕ главный поток
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true // кнопка стала активной после подготовки
            playerState = STATE_PREPARED // ввели состояние плейера в состояние "готов"
        }
        mediaPlayer.setOnCompletionListener { // вызывается после завершения воспроизведения. Кнопка с надписью ПЛЕЙ и состояние ГОТОВ
            play.setImageResource(R.drawable.button_play) // отображается иконка кнопки Play
            playerState = STATE_PREPARED
            handler.removeCallbacks(updateTimeRunnable) // остановка таймера при завершении трека
            timeTextView.text = getString(R.string.temporary_source_track_time_mills_player) //00:00
        }

    }

    // Функция запуска Аудиоплейера
    private fun startPlayer() {
        mediaPlayer.start() // вызывает метод Старт медиаплейера
        play.setImageResource(R.drawable.button_pause) // // отображается иконка кнопки Pause
        playerState = STATE_PLAYING // меняем состояние плейера на STATE_PLAYING
        handler.post(updateTimeRunnable) // запуск обновления времени проигрывания
    }

    // Функция паузы Аудиоплейера
    private fun pausePlayer() {
        mediaPlayer.pause() // вызываем метода Пауза
        play.setImageResource(R.drawable.button_play) // отображается иконка кнопки Play
        playerState = STATE_PAUSED // // меняем состояние плейера на STATE_PAUSED
        handler.removeCallbacks(updateTimeRunnable) // остановка обновления проигрывания
    }

    // Функция выбора подходящего действия
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer() // eсли текущее состояние медиаплеера равно STATE_PLAYING, то нажатие на кнопку должно ставить воспроизведение на паузу (вызываем функцию pausePlayer())
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer() // если текущее состояние STATE_PAUSED или STATE_PREPARED, то нажатие на кнопку должно запускать воспроизведение (вызываем функцию startPlayer())
            }
        }
    }*/

    // Функции для корректной работы плейера в соответсвии с жизненным циклом Активити

    // Если пользователь НЕ на экране плейера (свернул активити через кнопку Home или запускает другое приложение), то проигрывание на ПАУЗУ
    override fun onPause() {
        super.onPause()
        if (interactor.getState() == PlayerState.PLAYING) {
            interactor.playbackControl() // поставить на паузу
            play.setImageResource(R.drawable.button_play)
            handler.removeCallbacks(updateTimeRunnable) // остановка обновления проигрывания
        }
        //pausePlayer()
    }

    // Если пользователь закрыл активити и медиаплеер и его возможности больше не нужны чтобы освободить память и ресурсы процессора, выделенные системой при подготовке медиаплеера
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimeRunnable) // остановка таймера
        interactor.release() // вывод плейера из подготовки
    }

}