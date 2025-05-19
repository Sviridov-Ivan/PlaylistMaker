package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonMedia = findViewById<Button>(R.id.button_media)
        val buttonSettings = findViewById<Button>(R.id.button_settings)


        //Взаимодействие с кнопкой ПОИСК через реализацию анонимного класса (отображение тоста)
        /*val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearch.setOnClickListener(buttonSearchClickListener)


        //Взаимодействие с кнопкой ПОИСК через лямбду (отображение тоста)
        buttonSearch.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск!", Toast.LENGTH_SHORT).show()
        }

        //Взаимодействие с кнопкой МЕДИАТЕКА через реализацию анонимного класса (отображение тоста)

        val buttonMediaClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonMedia.setOnClickListener(buttonMediaClickListener)


        //Взаимодействие с кнопкой МЕДИАТЕКА через лямбду (отображение тоста)
        buttonMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека!", Toast.LENGTH_SHORT).show()
        }

        //Взаимодействие с кнопкой Настройки через реализацию анонимного класса (отображение тоста)

        val buttonSettingsClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку Настройки!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSettings.setOnClickListener(buttonSettingsClickListener)


        //Взаимодействие с кнопкой Настройки через лямбду (отображение тоста)
        buttonSettings.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Настройки!", Toast.LENGTH_SHORT).show()
        }*/

        //Реализация перехода на экран Поиск (пока пустого)
        buttonSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        //Реализация перехода на экран Медиатека (пока пустого)
        buttonMedia.setOnClickListener {
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        //Реализация перехода на экран Настройки
        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }



    }
}




