package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

class App : Application() { // класс, созданный для смены темы через Switch и запоминания этого действия в SharedPreferences. Вызывается в SettingsActivity

    companion object { // постоянные решил определить здесь, не вне App (выше class App : Application()), так как планирую использовать только в этом классе
        const val PREFS_NAME = "settings" // сохранённые данные-значения с помощью Shared Preferences
        const val KEY_DARK_THEME = "dark_theme" // ключ для Shared Preferences
    }

    var darkTheme = false // хранить текущее состояние темы
    private lateinit var sharedPrefs: SharedPreferences // создание переменной для SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE) // присваивание переменной для SharedPreferences

        //darkTheme = sharedPrefs.getBoolean(KEY_DARK_THEME, false) // Загружаем/считываем сохранённое значение из SharedPreferences по ключу KEY_DARK_THEME, false значит, по умполчанию светлая тема (закомментил т.к. использую код ниже для синхронизации с темой Системы при первом запуске)

        if (sharedPrefs.contains(KEY_DARK_THEME)) { // код для синхронизации темы приложения с темой системы при первом запуске при получении данных из SharedPreferences
            darkTheme = sharedPrefs.getBoolean(KEY_DARK_THEME, false)
        } else {
            val isSystemInDarkMode = (resources.configuration.uiMode // Первый запуск — используется тема системы (далее, уже данные попадают в SharedPreferences и при самостоятельно смене темы приложения, чтобы получить соответствие, необхоимо удалять инфу в SharedPreferences
                    and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES

            darkTheme = isSystemInDarkMode

            sharedPrefs.edit {
                putBoolean(KEY_DARK_THEME, darkTheme)
            }
        }

        // Применяем тему (которую извлекли из SharedPreferences)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    // Функция для ручной смены темы через Switch
    fun switchTheme(darkThemeEnabled: Boolean) {
        //if (darkTheme == darkThemeEnabled) return // если уже есть темная, то возврат
        darkTheme = darkThemeEnabled

        // Применяем тему (без этого не меняет тему через свитч)
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        // Сохраняем тему после изменения (Boolean так как для положение 0 или 1)
        sharedPrefs.edit()
            .putBoolean(KEY_DARK_THEME, darkTheme)
            .apply()

    }
}