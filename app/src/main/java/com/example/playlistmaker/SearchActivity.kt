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

class SearchActivity : AppCompatActivity() {

    private var currentSearchText: String = "" // создание приватной переменной для использования в fun onTextChanged и fun onSaveInstanceState для сохранения введенных данных при развороте экрана (хотя достаточно присвоить id для EditText)

    private fun hideKeyboard(view: View) { // функция для скрытия клавиатуры
        val keyboardUse = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardUse.hideSoftInputFromWindow(view.windowToken,0)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //Реализация возврата на стартовый экран
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        viewArrowBackToMain.setOnClickListener {
            finish() // закрываю текущую активность
        }

        val inputEditText = findViewById<EditText>(R.id.inputEditText) // создание переменной для работы с элементом EditText из разметки
        val clearButton = findViewById<ImageView>(R.id.clearIcon) // создание переменной для работы с элементом ImageView из разметки

        clearButton.setOnClickListener { //метод удаления информацции при нажатии на кнопку сброса (точнее выводит пустой текст "")
            inputEditText.setText("")
            inputEditText.clearFocus() // убираю фокус, чтобы клавиатура не появлялась снова
            hideKeyboard(inputEditText) // функция для скрытия клавиатуры
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
                currentSearchText = s.toString() // производим преобразование CharSequence → String с помощью toString(), так как функция onTextChanged имеет тип CharSequence? а Bundle.putString() и setText() работают с типом String.
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher) // добавление TextWatcherа
    }

    private fun clearButtonVisibility(s: CharSequence?): Int { // функция для обработки видимости кнопки сбороса введенных данных
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) { // переопределение метода onSaveInstanceState, и сохранение в нем текст из EditText в Bundle методом putString.
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) { // переопределение метода onRestoreInstanceState, извлечение данныех из Bundle при помощи метода getString.
        super.onRestoreInstanceState(savedInstanceState)

        val restoredText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        val inputEditText = findViewById<EditText>(R.id.inputEditText) // создание переменной для работы с элементом EditText из разметки
        val clearButton = findViewById<ImageView>(R.id.clearIcon) // создание переменной для работы с элементом ImageView из разметки

        inputEditText.setText(restoredText) // установление восстановленных данных обратно в EditText при помощи функции .setText(value).
        clearButton.visibility = if (restoredText.isNullOrEmpty()) { // то же с кнопкой сброса
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT" //создание константы для ключей хранения данных
    }
}