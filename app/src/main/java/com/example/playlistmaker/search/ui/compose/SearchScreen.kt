package com.example.playlistmaker.search.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.compose.TrackItem
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.search.ui.model.PlaceholderState
import com.example.playlistmaker.ui.RoundedButton


@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {

    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(WindowInsets.systemBars.asPaddingValues()) // вместо реализации edge by edge
    ) {

        SearchTitle()

        Spacer(modifier = Modifier.size(30.dp))

        SearchField(
            text = state.query,
            onTextChange = viewModel::onQueryChanged,
            onClear = {
                viewModel.onQueryChanged("")
            }
        )

        Spacer(modifier = Modifier.size(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)   // для отделения фиксированной части от контентной
        ) {

            when {

                state.isLoading -> {
                    LoadingIndicator()
                }

                state.placeholder == PlaceholderState.Error -> {
                    ErrorPlaceholder { viewModel.retry() }
                }

                state.placeholder == PlaceholderState.Empty -> {
                    EmptyPlaceholder()
                }

                else -> {

                    TracksList(
                        tracks = state.tracks,
                        showHistory = state.placeholder == PlaceholderState.History,
                        onTrackClick = {
                            viewModel.onTrackClicked(it)
                            onTrackClick(it)
                        },
                        onClearHistory = viewModel::clearHistory
                    )
                }
            }
        }
    }
}

@Composable
fun SearchTitle() {

    Text(
        text = stringResource(R.string.search_button),
        fontFamily = FontFamily(Font(R.font.ys_display_medium)),
        fontSize = dimensionResource(R.dimen.main_text_size).value.sp,
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

@Composable
fun SearchField(
    text: String,
    onTextChange: (String) -> Unit,
    onClear: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(45.dp) // точная высота
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ),

        contentAlignment = Alignment.CenterStart
    ) {
        // кастомное поле для ввода текста для поиска (можно использовать OutlinedTextField, но нужно обновить Material3 до 1.4)
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontSize = 16.sp,
                color = colorResource(R.color.black)
            ),
            modifier = Modifier.fillMaxWidth(),
            cursorBrush = SolidColor(colorResource(R.color.blue)), // цвет курсора
            decorationBox = { innerTextField ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp), // внутренние отступы слева/справа для поля ввода!!! можно разместить в Box()
                ) {
                    Icon(
                        painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiaryContainer
                    )

                    Spacer(modifier = Modifier.width(8.5.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (text.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_input_hint),
                                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                        innerTextField()
                    }

                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                onClear()
                            },
                            modifier = Modifier.padding(end = 4.dp) // отступ от правого края для Х
                        ) {
                            Icon(
                                painterResource(R.drawable.clear),
                                contentDescription = stringResource(R.string.contentDescription_clear_icon),
                                tint = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun TracksList(
    tracks: List<Track>,
    showHistory: Boolean,
    onTrackClick: (Track) -> Unit,
    onClearHistory: () -> Unit
) {

    Column {

        if (showHistory) {

            Text(
                text = stringResource(R.string.history_title),
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = dimensionResource(R.dimen.p_h_text_view_text_size).value.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 20.dp),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)      // занимает все свободное пространство (между "Вы искали" и кнопкой)
                .fillMaxWidth()
        ) {

            items(
                items = tracks,
                key = { it.trackId } // для улучшения производительности списка

            ) { track ->

                TrackItem(
                    track = track,
                    onClick = { onTrackClick(track) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (showHistory && tracks.isNotEmpty()) {

            RoundedButton(
                onClick = onClearHistory,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp),
                text = stringResource(R.string.clear_history_button),
                textColor = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun LoadingIndicator() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp), // 140 - 24 spacer,
        contentAlignment = Alignment.TopCenter
    ) {

        CircularProgressIndicator(
            modifier = Modifier.size(44.dp)
        )
    }
}

@Composable
fun EmptyPlaceholder() {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 76.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        PlaceholderContent(
            image = R.drawable.error_nothing,
            text = stringResource(R.string.nothing_found)
        )
    }
}

@Composable
fun ErrorPlaceholder(
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 76.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PlaceholderContent(
                image = R.drawable.error_nonet,
                text = stringResource(R.string.something_went_wrong)
            )

            Spacer(modifier = Modifier.height(24.dp))

            RoundedButton(
                onClick = onRetry,
                text = stringResource(R.string.refresh),
                textColor = MaterialTheme.colorScheme.secondary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PlaceholderContent(
    image: Int,
    text: String
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(image),
            contentDescription = null,
            modifier = Modifier.size(
                dimensionResource(R.dimen.p_h_image_size)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                fontSize = 19.sp
            )
        )
    }
}


