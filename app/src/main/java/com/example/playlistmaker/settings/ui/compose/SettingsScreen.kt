package com.example.playlistmaker.settings.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.SettingsViewModel
import com.example.playlistmaker.ui.CustomThemeSwitcher

// Основной экран
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {

    val isDarkTheme by viewModel.observeIsDarkTheme().observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(WindowInsets.systemBars.asPaddingValues()) // вместо реализации edge by edge
    ) {
        // Заголовок
        SettingsTitle()

        // Переключатель темы
        ThemeSwitcher(
            checked = isDarkTheme,
            onCheckedChange = { viewModel.toggleTheme(it) }
        )

        Spacer(modifier = Modifier.size(16.dp))

        // Элементы настроек по порядку
        SettingsItem(
            text = stringResource(R.string.share_app),
            icon = R.drawable.light_mode_4,
            onClick = { viewModel.onShareAppClicked() }
        )

        SettingsItem(
            text = stringResource(R.string.support),
            icon = R.drawable.light_mode_5,
            onClick = { viewModel.onSupportClicked() }
        )

        SettingsItem(
            text = stringResource(R.string.agreement),
            icon = R.drawable.light_mode_6,
            onClick = { viewModel.onTermsClicked() }
        )
    }
}

// Заголовок
@Composable
fun SettingsTitle() {
    Text(text = stringResource(R.string.settings_button),
        fontSize = dimensionResource(R.dimen.main_text_size).value.sp,
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(dimensionResource(R.dimen.layout_name_settings))
            .padding(
                start = dimensionResource(R.dimen.compose_padding_title_top_start),
                top = dimensionResource(R.dimen.compose_padding_title_top_start),
            )
    )
}

// Переключатель темы
@Composable
fun ThemeSwitcher(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row( modifier = Modifier
        .fillMaxWidth()
        .padding(
            start = dimensionResource(R.dimen.settings_text_view_left),
            end = dimensionResource(R.dimen.settings_image_view_right),
            top = 24.dp
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Темная тема",
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = dimensionResource(R.dimen.settings_text_size).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular))
        )

        CustomThemeSwitcher(
            checked = checked,
            onCheckedChange = onCheckedChange

        )
    }
}

// Компонент строки настроек
@Composable
fun SettingsItem(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                //indication = rememberRipple( color = MaterialTheme.colorScheme.onSecondary),
                onClick =onClick
            )
            .padding(
                start = dimensionResource(R.dimen.settings_text_view_left),
                end = dimensionResource(R.dimen.settings_image_view_right),
                top = dimensionResource(R.dimen.settings_image_view_top),
                bottom = dimensionResource(R.dimen.settings_image_view_bottom)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = dimensionResource(R.dimen.settings_text_size).value.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular))
        )

        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.tertiary,
        )
    }
}



