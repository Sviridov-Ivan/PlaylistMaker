package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge() // пока не использую, так как работаем оп макетам Figma (потребуется использовать
        setContentView(R.layout.activity_settings)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.message)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val viewArrowBackToMain = findViewById<ImageView>(R.id.arrow_back_to_main)

        //Реализация возврата на стартовый экран
        viewArrowBackToMain.setOnClickListener {
            val backToMainIntent = Intent(this, MainActivity::class.java)
            startActivity(backToMainIntent)
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
    }
}