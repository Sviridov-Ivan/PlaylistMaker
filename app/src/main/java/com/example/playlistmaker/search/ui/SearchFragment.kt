package com.example.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.adapter.TracksAdapter
import com.example.playlistmaker.databinding.FragmentSearchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import kotlin.toString

class SearchFragment : Fragment() {

    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT" //создание константы для ключей хранения данных (для строки поиска) (Спринт 15)

        private const val CLICK_DEBOUNCE_DELAY = 1000L // задержка на открытия активити AudioPlayerActivity (Спринт 14)

        private const val SEARCH_DEBOUNCE_DELAY = 2000L // задержка для начала отправки запроса после введенного текста в строке поиска (Спринт 14)

    }
    private lateinit var binding: FragmentSearchBinding

    private val viewModel: SearchViewModel by viewModel() // vieModel через SearchModule.kt с исп.Koin

    private var isClickAllowed = true // глобальная переменная для использования в Debounce (Спринт 14) флаг защиты от повторных кликов
    private val handler =
        Handler(Looper.getMainLooper()) // переменная для доступа к Handler и Looper основного потока UI и внесение в него корректировки (Спринт 14)
    private val searchRunnable = Runnable { performSearch(binding.inputEditText.text.toString()) } // переменная для дальнейшего использования в функции для отложенного отслеживания ввода для автопоиска (Спринт 14)

    private val adapter =
        TracksAdapter() // создаем переменную для адаптера с пустым конструктором (там есть пометка)

    private var currentSearchText: String = "" // создание приватной переменной для использования в fun onTextChanged и fun onSaveInstanceState для сохранения введенных данных при развороте экрана (хотя достаточно присвоить id для EditText)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.searchLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // настройка RecyclerView
        // поправил использование Context
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false) // вызываем адаптер для LinearLayoutManager (составляющий элемент RecyclerView помимо адаптера и вьюхолдера)
        binding.recyclerView.adapter = adapter

        // oбработка клика по элементу списка RecyclerView треков и сохранение в историю (Спринт 12 + 13 + 14 + замена в 15)
        adapter.setOnItemClickListener { track ->
            if (clickDebounce()) { // реализация дебонса - задержки на открытие активити на CLICK_DEBOUNCE_DELAY при нажатии (спринт 14)
                viewModel.saveTrack(track) // сохраняю трек в историю по клику на отображенном списке поиска (вызывается до tracksList.adapter = adapter // адаптер для RecyclerView) (в классе TrackAdapter)

                val bundle = Bundle().apply {
                    putParcelable("track", track)
                }

             // переход на экрна AudioPlayer
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment, bundle
                )
            }
        }

        // поиск при нажатии на кнопку "Поиск" на клавиатуре
        // в 14 спринте уже используется код в TextWatcher в onTextChanged для отправки запроса, но данный вариант пока оставил
        binding.inputEditText.setOnEditorActionListener {_, actionId, _ -> // вызываем функцию performSearch для обработки запроса от iTunesBaseUrl, но через галочку IME_ACTION_DONE на клавиатуре по условию 11 спринта
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = binding.inputEditText.text.toString()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        // кнопка "повторить" на экране ошибки
        binding.placeholderButton.setOnClickListener { // повторный вызов функции performSearch через кнопку плейсхолдера при ошибке загрузки, она только тогда появляется
            performSearch(binding.inputEditText.text.toString())
        }

        // иконка очистки строки поиска
        binding.clearIcon.setOnClickListener { //метод удаления информацции при нажатии на кнопку сброса (точнее выводит пустой текст "") (для строки поиска)
            binding.inputEditText.setText("")
            binding.inputEditText.clearFocus() // убираю фокус, чтобы клавиатура не появлялась снова (для строки поиска)
            hideKeyboard(binding.inputEditText) // функция для скрытия клавиатуры (для строки поиска)
            adapter.clearTracks()// использую функцию из Адаптера для очистки списка треков
            viewModel.showHistory()
        }

        // TextWatcher для поля поиска
        val simpleTextWatcher = object : TextWatcher { // переменная для работы с отслеживанием введенного текста
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { //(для строки поиска)
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                currentSearchText = s.toString() // производим преобразование CharSequence → String с помощью toString(), так как функция onTextChanged имеет тип CharSequence? а Bundle.putString() и setText() работают с типом String. (для строки поиска)
                binding.clearIcon.visibility = clearButtonVisibility(s) // отображение кнопочки R.id.clearIcon для очистки строки поиска

                if (s.isNullOrEmpty()) {
                    adapter.clearTracks() // использую функцию из Адаптера для очистки списка треков (только из области видимости, не из SharedPreferences)
                    //placeholderContainer.visibility = View.GONE // убираю плейсхолдер, пока не нужно
                    //hidePlaceholder() // вызов функции для скрытия плейсхолдера
                    //showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12) !!! закоментил, так как буду показывать исторю, если поле в фокусе

                    // показываем историю только если поле в фокусе
                    if (binding.inputEditText.hasFocus()) {
                        viewModel.showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
                    } else {
                        binding.historyTitle.visibility = View.GONE
                        binding.clearHistoryButton.visibility = View.GONE
                    }
                } else {
                    // скрытие и очистка (из видимости) элементов истории при начале ввода
                    adapter.clearTracks()
                    binding.historyTitle.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE
                    searchDebounce() // функция для реализации отложенного на SEARCH_DEBOUNCE_DELAY отправки запроса (Спринт 14)
                }
            }
            override fun afterTextChanged(s: Editable?) {  //(для строки поиска)
                // empty
            }
        }
        binding.inputEditText.addTextChangedListener(simpleTextWatcher) // добавление TextWatcherа (для строки поиска)

        // кнопка очистки истории поиска
        binding.clearHistoryButton.setOnClickListener { // обработка нажатия на кнопки Очистить историю (Спринт 12)
            viewModel.clearHistory() // вызов функции clearHistory() из экземпляра класса searchHistory
            viewModel.showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
        }

        // для отслеживания СОСТОЯНИЯ фокуса и пустоты поля ввода - если начат ввод, тогда историю скрыть (Спринт 12)
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.inputEditText.text.isEmpty()) {
                viewModel.showHistory()
            }
        }

        // Восстановление поиска текста (доп. Спринт 12) при перевороте экрана
        if (savedInstanceState != null) {
            currentSearchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
            binding.inputEditText.setText(currentSearchText)
        }
        if (currentSearchText.isEmpty()) {
            viewModel.showHistory() // функция отображения истории, создана ниже вне onCreate (Спринт 12)
        }

        // при пустой строке сразу показать историю
        if (currentSearchText.isEmpty()) {
            viewModel.showHistory()
        }

        // подписка на LiveData
        observeViewModel()
    }

    // Подписка на LiveData из ViewModel
    private fun observeViewModel() { // заменил LifecycleOwner на ViewLifecycleOwner
        // список треков
        viewModel.observeTracks().observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
        }

        // загрузка (показать/скрыть ProgressBar)
        viewModel.observeLoading().observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // состояние плейсхолдеров (ошибка, пусто, история, обычный список)
        viewModel.observePlaceholderState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchViewModel.PlaceholderState.None -> {
                    binding.placeholderContainer.visibility = View.GONE
                    binding.historyTitle.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE
                }
                is SearchViewModel.PlaceholderState.Empty -> {
                    binding.placeholderContainer.visibility = View.VISIBLE
                    binding.placeholderImage.setImageResource(R.drawable.error_nothing)
                    binding.placeholderText.setText(R.string.nothing_found)
                    binding.placeholderButton.visibility = View.GONE
                }
                is SearchViewModel.PlaceholderState.Error -> {
                    binding.placeholderContainer.visibility = View.VISIBLE
                    binding.placeholderImage.setImageResource(R.drawable.error_nonet)
                    binding.placeholderText.setText(R.string.something_went_wrong)
                    binding.placeholderButton.visibility = View.VISIBLE
                    binding.historyTitle.visibility = View.GONE
                    binding.clearHistoryButton.visibility = View.GONE
                }
                is SearchViewModel.PlaceholderState.History -> {
                    binding.historyTitle.visibility = View.VISIBLE
                    binding.clearHistoryButton.visibility = View.VISIBLE
                    binding.placeholderContainer.visibility = View.GONE
                }
            }
        }
    }

    // Выполнить поиск через ViewModel
    private fun performSearch(query:String) { // функция для реализации запроса (Спринт 15 сам запрос в TracksRepositoryImpl)
        viewModel.searchTracks(query)
    }

    // скрытие клавиатуры
    // поправил использование Context
    private fun hideKeyboard(view: View) { // функция для скрытия клавиатуры
        val keyboardUse = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        keyboardUse.hideSoftInputFromWindow(view.windowToken,0)
    }

    // функция для реализации Debounce - задержки открытия активити Poster Activity воизбежании многократного открытия одного и того же экрана (Спринт 14)
    // если isClickAllowed тру, меняем на фолс и через handler с задержкой меняем назад на тру
    // используется в обработке нажатия
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }

    // функция для реализации отложенного на SEARCH_DEBOUNCE_DELAY отправики запроса от введенных символов в едит текст. Вызвана в override fun onTextChanged для TextWatcher
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable) // мы удаляем последнюю запланированную отправку запроса и тут же
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY) // используя метод postDelayed(), планируем запуск этого же Runnable через две секунды.
    }

    // скрытие кнопки очистки ввода
    private fun clearButtonVisibility(s: CharSequence?): Int { // функция для обработки видимости кнопки сбороса введенных данных (для строки поиска)
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    // Сохранение текста при перевороте экрана
    override fun onSaveInstanceState(outState: Bundle) { // переопределение метода onSaveInstanceState, и сохранение в нем текст из EditText в Bundle методом putString. (для строки поиска)
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentSearchText)
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    // Не стал реализовывать override fun onRestoreInstanceState(savedInstanceState: Bundle)
}