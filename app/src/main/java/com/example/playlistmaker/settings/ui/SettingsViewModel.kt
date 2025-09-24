package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData
import com.example.playlistmaker.util.SingleLiveEvent

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    // Тема
    private val isDarkTheme = MutableLiveData<Boolean>()
    fun observeIsDarkTheme(): LiveData<Boolean> = isDarkTheme

    // События
    private val shareAppEvent = SingleLiveEvent<String>() // одноразовое событие
    fun observeShareAppEvent(): LiveData<String> = shareAppEvent

    private val openTermsEvent = SingleLiveEvent<String>() // одноразовое событие
    fun observeOpenTermsEvent(): LiveData<String> = openTermsEvent

    private val supportEmailEvent = SingleLiveEvent<EmailData>() // одноразовое событие
    fun observeSupportEmailEvent(): LiveData<EmailData> = supportEmailEvent

    init {
        isDarkTheme.value = settingsInteractor.isDarkModeEnable()
    }

    // UI actions
    fun toggleTheme(enabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(enabled)
        isDarkTheme.value = enabled
    }

    fun onShareAppClicked() {
        shareAppEvent.value = sharingInteractor.getShareAppLink()
    }

    fun onTermsClicked() {
        openTermsEvent.value = sharingInteractor.getTermsLink()
    }

    fun onSupportClicked() {
        supportEmailEvent.value = sharingInteractor.getSupportEmailData()
    }
}
