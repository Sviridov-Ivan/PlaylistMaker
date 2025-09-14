package com.example.playlistmaker.sharing.domain.interactor
import com.example.playlistmaker.sharing.domain.model.EmailData

interface SharingInteractor {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
}