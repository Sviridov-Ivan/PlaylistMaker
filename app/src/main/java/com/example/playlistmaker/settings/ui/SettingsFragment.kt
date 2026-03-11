package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.settings.ui.compose.SettingsScreen
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var navigator: ExternalNavigatorImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator = ExternalNavigatorImpl(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {

            setContent {

                PlaylistMakerTheme { // обернут для использования моей цветовой гаммы

                    SettingsScreen(viewModel) // compose функция
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // переключение темы приложения
        viewModel.observeIsDarkTheme().observe(viewLifecycleOwner) { enabled ->
            App.switchTheme(enabled)
        }

        // события из ViewModel
        // поделится приложением
        viewModel.observeShareAppEvent().observe(viewLifecycleOwner) { link ->
            navigator.shareLink(link)
        }

        // соглашение
        viewModel.observeOpenTermsEvent().observe(viewLifecycleOwner) { link ->
            navigator.openLink(link)
        }

        // написать в поддержку
        viewModel.observeSupportEmailEvent().observe(viewLifecycleOwner) { emailData ->
            navigator.openEmail(emailData)
        }
    }
}