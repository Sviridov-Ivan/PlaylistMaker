package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import androidx.compose.foundation.lazy.items

@Composable
fun FavouriteTracksScreen(
    tracks: List<Track>,
    isPlaceholderVisible: Boolean,
    onTrackClick: (Track) -> Unit
) {
    val scrollState = rememberLazyListState() // для фиксации состояния прокрутки

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp) // отступ сверху от Tablelayout
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        if (isPlaceholderVisible) {
            // Placeholder
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 106.dp), // p_h_media_favourite_padding_top
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.error_nothing),
                    contentDescription = stringResource(R.string.contentDescription_place_holder),
                    modifier = Modifier.size(120.dp), // p_h_image_size
                    contentScale = ContentScale.Inside
                )
                Text(
                    text = stringResource(R.string.media_is_empty),
                    modifier = Modifier.padding(
                        top = 16.dp,  // p_h_text_view_margin_top
                        start = 24.dp, // p_h_text_view_padding_start_end
                        end = 24.dp
                    ),
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    fontSize = 19.sp, // p_h_text_view_text_size
                    color = MaterialTheme.colorScheme.inversePrimary // colorForTitlesNameAudioPlayer
                )
            }
        } else {
            // аналог recyclerView
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(tracks) { track ->
                    TrackItem(track = track, onClick = { onTrackClick(track) })
                }
            }
        }
    }
}