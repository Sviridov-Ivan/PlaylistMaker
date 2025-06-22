package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private var currentSearchText: String = "" // создание приватной переменной для использования в fun onTextChanged и fun onSaveInstanceState для сохранения введенных данных при развороте экрана (хотя достаточно присвоить id для EditText)

    private fun hideKeyboard(view: View) { // функция для скрытия клавиатуры
        val keyboardUse = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardUse.hideSoftInputFromWindow(view.windowToken,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // РАБОТА С RecyclerView до 58 строки ВКЛЮЧИТЕЛЬНО
        // RecyclerView в коде (для составления списка треков), указав его в соответствующей разметке (activity_search.xml) и добавили LinearLayoutManager
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // создание переменных и списков из названий треков, артистов, и таймингов
        val trackNames = listOf("Smells Like Teen Spirit", "Billie Jean", "Stayin' Alive", "Whole Lotta Love", "Sweet Child O'Mine") // создание списков имен для трэков для мок-объекта
        val artistNames = listOf("Nirvana", "Michael Jackson", "Bee Gees", "Led Zeppelin", "Guns N' Roses") // создание списков имен авторов для трэков для мок-объекта
        val trackTimes = listOf("5:01", "4:35", "4:10", "5:33", "5:03") // создание списков времен проигрывания для трэков для мок-объекта
        val artworkUrl100s = listOf(
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg", // создание списков ссылок для загрузки картинок для трэков
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg", // создание списков ссылок для загрузки картинок для трэков
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg", // создание списков ссылок для загрузки картинок для трэков
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg", // создание списков ссылок для загрузки картинок для трэков
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg") // создание списков ссылок для загрузки картинок для трэков

        // создание переменной и общего списка из выщеуказанных списков и дата класса
        val tracks = trackNames.indices.map { i ->
            Track(
                trackName = trackNames[i],
                artistName = artistNames[i],
                trackTime = trackTimes[i],
                artworkUrl100 = artworkUrl100s[i]
            )
        }

        // передача переменной с общим списком в recyclerView.adapter (в xml)
        val tracksAdapter = TracksAdapter(tracks)
        recyclerView.adapter = tracksAdapter
        

        //Реализация возврата на стартовый экран
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        viewArrowBackToMain.setOnClickListener {
            finish() // закрываю текущую активность
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText) // создание переменной для работы с элементом EditText из разметки (для строки поиска)
        val clearButton = findViewById<ImageView>(R.id.clearIcon) // создание переменной для работы с элементом ImageView из разметки (для строки поиска)

        clearButton.setOnClickListener { //метод удаления информацции при нажатии на кнопку сброса (точнее выводит пустой текст "") (для строки поиска)
            inputEditText.setText("")
            inputEditText.clearFocus() // убираю фокус, чтобы клавиатура не появлялась снова (для строки поиска)
            hideKeyboard(inputEditText) // функция для скрытия клавиатуры (для строки поиска)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { //(для строки поиска)
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
                currentSearchText = s.toString() // производим преобразование CharSequence → String с помощью toString(), так как функция onTextChanged имеет тип CharSequence? а Bundle.putString() и setText() работают с типом String. (для строки поиска)
                clearButton.visibility = clearButtonVisibility(s) // (для строки поиска)
            }

            override fun afterTextChanged(s: Editable?) {  //(для строки поиска)
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher) // добавление TextWatcherа (для строки поиска)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int { // функция для обработки видимости кнопки сбороса введенных данных (для строки поиска)
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) { // переопределение метода onSaveInstanceState, и сохранение в нем текст из EditText в Bundle методом putString. (для строки поиска)
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // переопределение метода onRestoreInstanceState, извлечение данныех из Bundle при помощи метода getString. (для строки поиска)
        super.onRestoreInstanceState(savedInstanceState)

        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "") //(для строки поиска)
        val inputEditText = findViewById<EditText>(R.id.inputEditText) // создание переменной для работы с элементом EditText из разметки (для строки поиска)
        val clearButton = findViewById<ImageView>(R.id.clearIcon) // создание переменной для работы с элементом ImageView из разметки (для строки поиска)

        inputEditText.setText(restoredText) // установление восстановленных данных обратно в EditText при помощи функции .setText(value). (для строки поиска)
        clearButton.visibility = if (restoredText.isNullOrEmpty()) { // то же с кнопкой сброса (для строки поиска)
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT" //создание константы для ключей хранения данных (для строки поиска)

    }
}