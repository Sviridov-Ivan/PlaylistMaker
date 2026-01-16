package com.example.playlistmaker.sharing.data

import android.app.Activity
import com.example.playlistmaker.sharing.domain.model.EmailData

interface ExternalNavigator { // выход в Android (Activity, Intent) — чтобы интерактор не зависел от Android SDK напрямую
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)

    fun shareText(activity: Activity, text: String) // функция для передачи инфы о плейлисте в фиче Медиа
}