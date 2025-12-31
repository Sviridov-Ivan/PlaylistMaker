package com.example.playlistmaker.main.ui

import com.example.playlistmaker.R
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        // из-за проблем обратной совместимости Navigation Component
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment // подключение NavHostFragment к контейнеру фрагментов в RootActivity в androidx.fragment.app.FragmentContainerView // из-за проблем обратной совместимости Navigation Component
        val navController = navHostFragment.navController // Получаем экземпляр NavController у найденного навигационного фрагмента

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        // сокрытие bottomNavigationView при переходе на фрагмент AudioPlayerFragment, NewPlaylistFragment or PlaylistFragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.visibility = if (
                destination.id == R.id.audioPlayerFragment ||
                destination.id == R.id.addPlaylistFragment ||
                destination.id == R.id.editPlaylistFragment||
                destination.id == R.id.playlistFragment
            ) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }
}