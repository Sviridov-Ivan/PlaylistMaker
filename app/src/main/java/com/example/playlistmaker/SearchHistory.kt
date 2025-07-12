package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.removeLast
import androidx.core.content.edit

class SearchHistory(private val sharedPref: SharedPreferences) { // класс для работы с историей с исползованием SharedPreferences и объектом SharedPreferences в конструкторе

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history" // ключ для SharedPreferences
        private const val MAX_TRACKS = 10 // максимальное количество элементов в истории
    }

    private val gson = Gson() // используем библиотеку Gson для сериализации (десиреализации) данных для хранения в SharedPreferences

    fun getHistory(): ArrayList<Track> { // функция для отображения списка истории
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) // переменная json для преобразования и сохранения данных в SharedPreferences, а SharedPreferences хранит только строки
        return if (json != null) {
            val type = object : TypeToken<ArrayList<Track>>() {}.type // преобразовываю список треков ArrayList<Track> в строку (JSON) при сохранении и обратно при загрузке. !!! gson.fromJson(json, ArrayList<Track>::class.java) НЕ ПОДХОДИТ!!! Gson не сможет корректно разобрать обобщённый тип (ArrayList<Track>) из-за type erasure в Java/Kotlin — во время выполнения тип Track стирается, и остаётся просто ArrayList (это вместе со строкой ниже)
            gson.fromJson(json, type)

        } else {
            arrayListOf()
        }
    }

    fun saveTrack(track: Track) { // функция для сохранения списка истории
        val history = getHistory()
        history.removeAll { it.trackId == track.trackId } //  удаляю из списка history все треки, у которых trackId совпадает с track.trackId
        history.add(0, track) // добавляю выбранный элемент в историю в начало списка (индекс 0)
        if (history.size > MAX_TRACKS) { // если размер истории более 100, то
            history.removeAt(history.lastIndex) // удаляю последний из списка history (history.removeLast()данный метод почему-то вызывает ошибку)
        }
        sharedPref.edit { putString(SEARCH_HISTORY_KEY, gson.toJson(history)) } // преобразовываю и добавляю изменения в history и в SharedPreferences. Данную запись sharedPrefs.edit().putString(SEARCH_HISTORY_KEY, gson.toJson(history)).apply() AndroidStudio рекомендовало поменять, хотя изначально ее использовал
    }

    fun clearHistory() {
        sharedPref.edit { remove(SEARCH_HISTORY_KEY) } // очищаю список и память в SharedPreferences по данному списку sharedPref.edit(). Данную запись sharedPrefs.edit().remove(SEARCH_HISTORY_KEY).apply() AndroidStudio рекомендовало поменять, хотя изначально ее использовал
    }
}