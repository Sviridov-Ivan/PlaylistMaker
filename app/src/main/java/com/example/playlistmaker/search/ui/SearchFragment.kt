package com.example.playlistmaker.search.ui

import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.compose.SearchScreen
import com.example.playlistmaker.util.NetworkReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val networkReceiver = NetworkReceiver() // объект класса NetworkReceiver для работы с отслеживанием наличия доступа к сети Интернет
    private var isReceiverRegistered = false // проверка зарегистрирован ли ресивер, если onPause() сработает раньше

    private val viewModel: SearchViewModel by viewModel() // vieModel через SearchModule.kt с исп.Koin

    private var currentSearchText: String = "" // (всегда инициализирована) создание приватной переменной для использования в fun onTextChanged и fun onSaveInstanceState для сохранения введенных данных при развороте экрана (хотя достаточно присвоить id для EditText)



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setContent {

                PlaylistMakerTheme {

                    SearchScreen(
                        viewModel = viewModel,
                        onTrackClick = { track ->

                            val bundle = Bundle().apply {
                                putParcelable("track", track)
                            }

                            findNavController().navigate(
                                R.id.action_searchFragment_to_audioPlayerFragment, bundle
                            )
                        }
                    )
                }
            }
        }
    }

    // Сохранение текста при перевороте экрана
    override fun onSaveInstanceState(outState: Bundle) { // переопределение метода onSaveInstanceState, и сохранение в нем текст из EditText в Bundle методом putString. (для строки поиска)
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, currentSearchText)
    }

    override fun onResume() {
        super.onResume()

        // регистрация ресивера здесь, чтобы избежать утечики памяти
        if (!isReceiverRegistered) { // проверка на регистрацию ресивера
            val filter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

            ContextCompat.registerReceiver(
                requireContext(),
                networkReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            isReceiverRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()
        // отмена регистрации ресивера, чтобы избежать утечек памяти, при выгрузке из фрагмента
        if (isReceiverRegistered) { // проверка на регистрацию ресивера
            requireContext().unregisterReceiver(networkReceiver)
            isReceiverRegistered = false
        }
    }
    companion object {
        private const val KEY_SEARCH_TEXT = "SEARCH_TEXT" // создание константы для ключей хранения данных (для строки поиска) (Спринт 15)

    }
}