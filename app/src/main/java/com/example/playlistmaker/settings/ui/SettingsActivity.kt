package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var navigator: ExternalNavigatorImpl // для связи с ExternalNavigatorImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // поддержка EdgeToEdge режима

        binding = ActivitySettingsBinding.inflate(layoutInflater) //
        setContentView(binding.root)

        //setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars()) // реализация отступов для системных элементов систем барс
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Интеракторы из App
        val app = applicationContext as App
        val settingsInteractor = app.settingsInteractor
        val sharingInteractor = app.sharingInteractor
        navigator = ExternalNavigatorImpl(this)

        // ViewModel через фабрику
        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.provideFactory(sharingInteractor, settingsInteractor)
        )[SettingsViewModel::class.java]

        // Навигация назад
        binding.arrowBackToMain.setOnClickListener { finish() } // закрываю текущую активность

        // Обработчики кнопок через ViewModel
        binding.shareLinearLayout.setOnClickListener { viewModel.onShareAppClicked() }
        binding.supportLinearLayout.setOnClickListener { viewModel.onSupportClicked() }
        binding.agreementLinearLayout.setOnClickListener { viewModel.onTermsClicked() }

        // Подписка на LiveData (тема)
        viewModel.observeIsDarkTheme().observe(this) { enabled ->
            binding.themeSwitcher.isChecked = enabled
            app.switchTheme(enabled)
        }

        // Подписка на события навигации
        /*viewModel.observeShareAppEvent().observe(this) {
            sharingInteractor.getShareAppLink()
        }

        viewModel.observeOpenTermsEvent().observe(this) {
            sharingInteractor.getTermsLink()
        }

        viewModel.observeSupportEmailEvent().observe(this) {
            sharingInteractor.getSupportEmailData()
        }*/
        viewModel.observeShareAppEvent().observe(this) { link ->
            navigator.shareLink(link) // ← используем ExternalNavigatorImpl
        }

        viewModel.observeOpenTermsEvent().observe(this) { link ->
            navigator.openLink(link)
        }

        viewModel.observeSupportEmailEvent().observe(this) { emailData ->
            navigator.openEmail(emailData)
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleTheme(isChecked)
        }
    }
}

