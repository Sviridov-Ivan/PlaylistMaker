package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesService = ITunesService.api // переменная для работы с ITunesService (ранее был описан здесь в активити)

    private lateinit var inputEditText: EditText // создание переменной для определения типа XML activity_search Edit text
    private lateinit var tracksList: RecyclerView // создание переменной для определения типа с XML activity_search RecyclerView
    private lateinit var placeholderContainer: LinearLayout // создание переменной для определения типа с XML activity_search placeholderContainer
    private lateinit var placeholderImage: ImageView // создание переменной для определения типа с XML activity_search placeholderImage
    private lateinit var placeholderText: TextView // создание переменной для определения типа с XML activity_search placeholderText
    private lateinit var placeholderButton: Button // определения типа с XML activity_search placeholderButton

    private lateinit var historyTitle: TextView // создание переменной для опредения типа с с XML activity_search @+id/historyTitle (Спринт 12)
    private lateinit var clearHistoryButton: Button // создание переменной для опредения типа с с XML activity_search @+id/clearHistoryButton (Спринт 12)

    //private val tracks = ArrayList<Track>() // создаем переменную для списка данных из дата класса Track !!! закомментил, так как создал функцию в Адаптере, чтобы не напутать со списками Спринт 11

    private lateinit var searchHistory: SearchHistory // определяем переменную для работы с классом SearchHistory (история поиска, Спринт 12)
    private val adapter = TracksAdapter() // создаем переменную для адаптера с пустым конструктором (там есть пометка)

    private fun performSearch(query:String) { // функция для реализации запроса. сделал отдельно, так как понадобится для кнопки в плейсхолдере
        if (query.isEmpty()) return

        iTunesService.search(query).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>,response: Response<TracksResponse>
            ) {
                if (response.code() == 200) { // статус запроса успешный
                    val results = response.body()?.results.orEmpty() // безопасный вывод, если results будут пустые
                    if (results.isNotEmpty()) {
                        adapter.updateTracks(results) // использую функцию из Адаптера для обновления списка треков
                        hidePlaceholder()
                    } else {
                        showEmptyState() // картинка и текст НИЧЕГО НЕ НАЙДЕНО
                    }
                } else {
                    showErrorState() // картинка, текст и кнопка для "ПРОБЛЕМА СО СВЯЗЬЮ"
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                showErrorState()
            }
        })
    }

    private fun hidePlaceholder() { // функция для скрытия плейсхолдера, сделал отдельно для возможного использования еще где-то, но вообще можно и напрямую включить в fun performSearch использую в performSearch
        placeholderContainer.visibility = View.GONE
    }

    private fun showEmptyState() { // функция для реализации условия, если ничего не найдено (ни одного трека) использую в performSearch
        placeholderContainer.visibility = View.VISIBLE // отобразить placeholderContainer из XML
        placeholderImage.setImageResource(R.drawable.error_nothing) // отобразить картинку НИЧЕГО НЕ НАШЛОСЬ
        placeholderText.setText(R.string.nothing_found) // отобразить текст НИЧЕГО НЕ НАШЛОСЬ
        placeholderButton.visibility = View.GONE // кнопку НЕ отображать
        adapter.clearTracks() // использую функцию из Адаптера для очистки списка треков
    }

    private fun showErrorState() { // функция для реализации условия, если что-то не так со связью (или статус отличный от 200) использую в performSearch
        placeholderContainer.visibility = View.VISIBLE  // отобразить placeholderContainer из XML
        placeholderImage.setImageResource(R.drawable.error_nonet) // отобразить картинку ПРОБЛЕМЫ СО СВЯЗЬЮ
        placeholderText.setText(R.string.something_went_wrong) // отобразить текст ПРОБЛЕМЫ СО СВЯЗЬЮ
        placeholderButton.visibility = View.VISIBLE // кнопку для Обновить Отображать
        adapter.clearTracks() // использую функцию из Адаптера для очистки списка треков
    }

    private var currentSearchText: String = "" // создание приватной переменной для использования в fun onTextChanged и fun onSaveInstanceState для сохранения введенных данных при развороте экрана (хотя достаточно присвоить id для EditText)

    private fun hideKeyboard(view: View) { // функция для скрытия клавиатуры
        val keyboardUse = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardUse.hideSoftInputFromWindow(view.windowToken,0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge() // поддержка EdgeToEdge режима
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.searchLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchHistory = SearchHistory(getSharedPreferences("search_prefs", Context.MODE_PRIVATE)) // переменная для работы с классом SearchHistory и получением данных из SharedPreferences (Спринт 12)

        tracksList = findViewById(R.id.recyclerView) // передача данных от переменной в XML
        placeholderContainer = findViewById(R.id.placeholderContainer) //передача данных от переменной в XML
        placeholderImage = findViewById(R.id.placeholderImage) // передача данных от переменной в XML
        placeholderText = findViewById(R.id.placeholderText) // передача данных от переменной в XML
        placeholderButton = findViewById(R.id.placeholderButton) // передача данных от переменной в XML
        inputEditText = findViewById(R.id.inputEditText) // создание переменной для работы с элементом EditText из разметки (для строки поиска)

        historyTitle = findViewById(R.id.historyTitle) // передача данных от переменной в XML - закголовок истории (Спринт 12)
        clearHistoryButton = findViewById(R.id.clearHistoryButton) // передача данных от переменной в XML - очистка истории (Спринт 12)

        // Обработка клика по элементу списка RecyclerView треков и сохранение в историю (Спринт 12 + 13)
        adapter.setOnItemClickListener { track ->
            searchHistory.saveTrack(track) // сохраняю трек в историю по клику на отображенном списке поиска (вызывается до tracksList.adapter = adapter // адаптер для RecyclerView) (в классе TrackAdapter)

            // обработка вызова экрана AudioPlayerActivity при нажатии на элемент списка RecyclerView из адаптера (Спринт 13)
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra(IntentKeys.EXTRA_TRACK,track)
            startActivity(intent)
        }

        tracksList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) // вызываем адаптер для LinearLayoutManager (составляющий элемент RecyclerView помимо адаптера и вьюхолдера)
        tracksList.adapter = adapter // адаптер для RecyclerView

        inputEditText.setOnEditorActionListener() {_, actionId, _ -> // вызываем функцию performSearch для обработки запроса от iTunesBaseUrl, но через галочку IME_ACTION_DONE на клавиатуре по условию 11 спринта
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = inputEditText.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        placeholderButton.setOnClickListener { // повторный вызов функции performSearch через кнопку плейсхолдера при ошибке загрузки, она только тогда появляется
            performSearch(inputEditText.text.toString())
        }


        //Реализация возврата на стартовый экран (а точнее открытие активити main)
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        viewArrowBackToMain.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
        }


        val clearButton = findViewById<ImageView>(R.id.clearIcon) // создание переменной для работы с элементом ImageView из разметки (для строки поиска)

        clearButton.setOnClickListener { //метод удаления информацции при нажатии на кнопку сброса (точнее выводит пустой текст "") (для строки поиска)
            inputEditText.setText("")
            inputEditText.clearFocus() // убираю фокус, чтобы клавиатура не появлялась снова (для строки поиска)
            hideKeyboard(inputEditText) // функция для скрытия клавиатуры (для строки поиска)
            adapter.clearTracks()// использую функцию из Адаптера для очистки списка треков
            showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
        }

        val simpleTextWatcher = object : TextWatcher { // переменная для работы с отслеживанием введенного текста
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { //(для строки поиска)
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
                currentSearchText = s.toString() // производим преобразование CharSequence → String с помощью toString(), так как функция onTextChanged имеет тип CharSequence? а Bundle.putString() и setText() работают с типом String. (для строки поиска)
                clearButton.visibility = clearButtonVisibility(s) // отображение кнопочки R.id.clearIcon для очистки строки поиска

                if (s.isNullOrEmpty()) {
                    adapter.clearTracks() // использую функцию из Адаптера для очистки списка треков (только из области видимости, не из SharedPreferences)
                    //placeholderContainer.visibility = View.GONE // убираю плейсхолдер, пока не нужно
                    hidePlaceholder() // вызов функции для скрытия плейсхолдера
                    //showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12) !!! закоментил, так как буду показывать исторю, если поле в фокусе

                    // показываем историю только если поле в фокусе
                    if (inputEditText.hasFocus()) {
                        showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
                    } else {
                        historyTitle.visibility = View.GONE
                        clearHistoryButton.visibility = View.GONE
                    }
                } else {
                    // скрытие и очистка (из видимости) элементов истории при начале ввода
                    adapter.clearTracks()
                    historyTitle.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {  //(для строки поиска)
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher) // добавление TextWatcherа (для строки поиска)

        clearHistoryButton.setOnClickListener { // обработка нажатия на кнопки Очистить историю (Спринт 12)
            searchHistory.clearHistory() // вызов функции clearHistory() из экземпляра класса searchHistory
            showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
        }

        // для отслеживания СОСТОЯНИЯ фокуса и пустоты поля ввода - если начат ввод, тогда историю скрыть (Спринт 12)
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                showHistory()
            } else if (!hasFocus) {
                historyTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE

                if (inputEditText.text.isEmpty()) {
                    adapter.clearTracks() // скрываю историю из списка (только из области видимости, не из SharedPreferences)
                }
            }
        }

        // Восстановление поиска текста (доп. Спринт 12)
        if (savedInstanceState != null) {
            currentSearchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            inputEditText.setText(currentSearchText)
        }
        if (currentSearchText.isEmpty()) {
            showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
        }
    }

    private fun showHistory() { // функция для работы с экземпляром класса searchHistory для получения истории из SharedPreferences и соответствующей разработки (Спринт 12)
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            adapter.updateTracks(history)
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        } else {
            adapter.clearTracks()
            historyTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
        }
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