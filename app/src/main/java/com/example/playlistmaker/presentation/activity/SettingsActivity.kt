package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // поддержка EdgeToEdge режима
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsLayout)) { v, insets -> // присваиваю id для головного layout в верстке
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()) // реализация отступов для системных элементов систем барс
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        //Реализация возврата на стартовый экран
        /*viewArrowBackToMain.setOnClickListener { // работал возврат до 12 спринта, теперь из-за активации switch и класса App из-за данной реализации начинается "мерцание экрана" после повторного изменения темы (вышел-зашел)
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
        }*/
        viewArrowBackToMain.setOnClickListener {
            finish() // закрываю текущую активность
        }

        //Реализация нажатия на «Поделиться приложением» с отркытием вариантов отправки ссылки через имеющиеся мессенджеры
        val shareLinearLayout = findViewById<LinearLayout>(R.id.share_linear_layout)


        shareLinearLayout.setOnClickListener {
            val linkToTheCourse = getString(R.string.link_to_the_course)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, linkToTheCourse)

            startActivity(Intent.createChooser(shareIntent, getString(R.string.dialog_choice_title))) // если нужно без выбора, тогда оставь startActivity(shareIntent)
        }

        //Реализация нажатия на "Написать в поддержку" с открытием почты с шаблоном адреса, названия письма и текста

        val supportLinearLayout = findViewById<LinearLayout>(R.id.support_linear_layout)

        supportLinearLayout.setOnClickListener {
            val supportEmail = getString(R.string.support_email) // адрес моей почты
            val subject = getString(R.string.email_subject) // тема письма
            val body = getString(R.string.email_body) // само письмо

            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail)) // адрес моей почты
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject) // тема письма
            emailIntent.putExtra(Intent.EXTRA_TEXT, body) // само письмо

            startActivity(emailIntent)
        }

        //Реализация нажатия на "Пользовательское соглашение" с открытием почты с открытием браузера по умолчаению, и ссылкой на веб-страницу оферты Практикума

        val agreementLinearLayout = findViewById<LinearLayout>(R.id.agreement_linear_layout)


        agreementLinearLayout.setOnClickListener {
            val agreementYandexPracticum = getString(R.string.agreememt_yap)

            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(agreementYandexPracticum))

            startActivity(browserIntent)
        }

        // РЕАЛИЗАЦИЯ ПЕРКЛЮЧАТЕЛЯ темы через Switch (т.к. ко всему приложению, то создал отдельный класс App)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        // 15 Спринт
        // Переключатель темы
        val app = applicationContext as App
        val settingsInteractor = app.settingsInteractor

        // Установить состояние при запуске
        themeSwitcher.isChecked = settingsInteractor.isDarkModeEnable()

        //
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.setDarkThemeEnabled(isChecked) // сохраняем
            app.switchTheme(isChecked) // применяем
        }

        /*// (до 15 Спринта)
        //themeSwitcher.thumbTintList = ContextCompat.getColorStateList(this, R.color.thumb_color) // управлением цветом переключателя из кода (тест)
        //themeSwitcher.trackTintList = ContextCompat.getColorStateList(this, R.color.track_color) // управлением цветом переключателя из кода (тест)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme // Установить текущее состояние при запуске

        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked -> // реализация ручного переключения темы с использованием класса App
            (applicationContext as App).switchTheme(isChecked)
        }

        // Альтернативный вариант реализации
        /*val app = applicationContext as App // создаем переменную для App
        // сначал отключаю слушатель (без этого экран мерцает и зацикливается)
        themeSwitcher.setOnCheckedChangeListener(null)
        // Установить текущее состояние при запуске
        themeSwitcher.isChecked = app.darkTheme
        // Обработка переключение темы через switch
        themeSwitcher.setOnCheckedChangeListener { switcher, isChecked ->
                app.switchTheme(isChecked)
        }*/ */
    }
}