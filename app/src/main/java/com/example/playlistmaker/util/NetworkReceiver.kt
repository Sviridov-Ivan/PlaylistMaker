package com.example.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

// класс для работы с широковещательными сообщениями и утилитой для отслеживания наличия доступа к сети Интернет
class NetworkReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (context == null) return

        if (intent?.action != "android.net.conn.CONNECTIVITY_CHANGE") return

        val connected = NetworkUtils.isInternetAvailable(context)

        if (!connected) {
            Toast.makeText(context, "Отсутствует подключение к сети Интернет", Toast.LENGTH_LONG).show()
        }
    }
}