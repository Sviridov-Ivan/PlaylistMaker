package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel() // vieModel через SearchModule.kt с исп.Koin

    private lateinit var navigator: ExternalNavigatorImpl // для связи с ExternalNavigatorImpl
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.settingsLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()) // реализация отступов для системных элементов систем барс
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navigator = ExternalNavigatorImpl(requireContext())

        // Обработчики кнопок через ViewModel
        binding.shareLinearLayout.setOnClickListener { viewModel.onShareAppClicked() }
        binding.supportLinearLayout.setOnClickListener { viewModel.onSupportClicked() }
        binding.agreementLinearLayout.setOnClickListener { viewModel.onTermsClicked() }

        // Подписка на LiveData (тема)
        viewModel.observeIsDarkTheme().observe(viewLifecycleOwner) { enabled ->
            binding.themeSwitcher.isChecked = enabled
            App.switchTheme(enabled)
        }

        viewModel.observeShareAppEvent().observe(viewLifecycleOwner) { link ->
            navigator.shareLink(link) // ← используем ExternalNavigatorImpl
        }

        viewModel.observeOpenTermsEvent().observe(viewLifecycleOwner) { link ->
            navigator.openLink(link)
        }

        viewModel.observeSupportEmailEvent().observe(viewLifecycleOwner) { emailData ->
            navigator.openEmail(emailData)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}