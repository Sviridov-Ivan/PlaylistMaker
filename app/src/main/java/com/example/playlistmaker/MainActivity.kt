package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //Взаимодействие с кнопкой ПОИСК через реализацию анонимного класса
        val buttonSearch = findViewById<Button>(R.id.button_search)

        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)

        //Взаимодействие с кнопкой МЕДИАТЕКА через реализацию анонимного класса
        val buttonMedia = findViewById<Button>(R.id.button_media)

        val buttonMediaClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonMedia.setOnClickListener(buttonMediaClickListener)


        val buttonSettings = findViewById<Button>(R.id.button_settings)

        val buttonSettingsClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Настройки!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSettings.setOnClickListener(buttonSettingsClickListener)



    }
}




