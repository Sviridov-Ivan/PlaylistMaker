package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.message)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        //Реализация возврата на стартовый экран
        viewArrowBackToMain.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
        }
    }
}