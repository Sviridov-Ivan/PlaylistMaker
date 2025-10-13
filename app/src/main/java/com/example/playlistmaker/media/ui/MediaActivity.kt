package com.example.playlistmaker.media.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMediaBinding
import com.google.android.material.tabs.TabLayoutMediator

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // поддержка EdgeToEdge режима

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge() // поддержка EdgeToEdge режима

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mediaLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupViewPager() // функция для работы с медиатором для tabLayout и viewPager)

        // Навигация назад (закрываю текущую активность)
        binding.arrowBackToMain.setOnClickListener { finish() }
    }

    private fun setupViewPager() {
        val adapter = MediaViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // TabLayoutMediator
        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.tab_favourite_tracks)
                1 -> tab.text = getString(R.string.tab_playlist)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Освобождаем медиатор, чтобы не было утечек
        tabMediator.detach()
    }
}