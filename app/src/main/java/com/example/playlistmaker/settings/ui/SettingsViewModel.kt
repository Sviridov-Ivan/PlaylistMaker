package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.interactor.SettingsInteractor
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    // Тема
    private val isDarkTheme = MutableLiveData<Boolean>()
    fun observeIsDarkTheme(): LiveData<Boolean> = isDarkTheme

    // События
    private val shareAppEvent = MutableLiveData<String>()
    fun observeShareAppEvent(): LiveData<String> = shareAppEvent

    private val openTermsEvent = MutableLiveData<String>()
    fun observeOpenTermsEvent(): LiveData<String> = openTermsEvent

    private val supportEmailEvent = MutableLiveData<EmailData>()
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

    companion object {
        fun provideFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                    return SettingsViewModel(sharingInteractor, settingsInteractor) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
            }
        }
    }
}
