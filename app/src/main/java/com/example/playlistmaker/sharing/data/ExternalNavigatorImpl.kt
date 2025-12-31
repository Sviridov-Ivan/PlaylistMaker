package com.example.playlistmaker.sharing.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator { // pеализация ExternalNavigator

    override fun shareLink(link: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    override fun openLink(link: String) { // пользовательское соглашение
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        context.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) { // написать разработчику
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(emailData.email))
            putExtra(Intent.EXTRA_SUBJECT, emailData.subject)
            putExtra(Intent.EXTRA_TEXT, emailData.body)
        }
        context.startActivity(intent)
    }

    // функция для передачи инфы о плейлисте в фиче Медиа
    override fun shareText(activity: Activity, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, null))
    }
}