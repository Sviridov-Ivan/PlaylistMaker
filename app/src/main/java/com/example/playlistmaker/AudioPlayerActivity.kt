package com.example.playlistmaker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.formatDuration

class AudioPlayerActivity : AppCompatActivity() {

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

        val radiusPx = dpToPx(8f, artworkView.context) // пребразование радиуса закругления углов картинок, указанных в Фигеме в рх для Glide для элемента artworkView


        // получение данных из @Parcelize дата класса Track
        track?.let {
            trackNameView.text = it.trackName
            artistNameView.text = it.artistName
            trackTimeView.text = formatDuration(it.trackTimeMillis) // используем отформатированное значение времени функция в файле TimeUtils
            //albumView.text = it.collectionName // обработка отображения ниже, в зависимости от наличия к треку
            //releaseYearView.text = it.releaseDate // обработка отображения ниже, в зависимости от наличия к треку
            //genreView.text = it.primaryGenreName
            //countryView.text = it.country

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
        }


        // Реализация возврата назад на экран SearchActivity, путем завершения AudioPlayerActivity
        viewArrowBackToSearchActivity.setOnClickListener {
            finish()
        }

    }
}