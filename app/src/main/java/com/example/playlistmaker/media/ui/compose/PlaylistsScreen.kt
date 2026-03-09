package com.example.playlistmaker.media.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.ui.RoundedButton

@Composable
fun PlaylistsScreen(
    playlists: List<Playlist>,
    isPlaceholderVisible: Boolean,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(top = 16.dp)
    ) {

        RoundedButton(
            onClick = onCreatePlaylistClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
                //.padding(bottom = 16.dp),
            text = stringResource(R.string.new_playlist),
            textColor = MaterialTheme.colorScheme.secondary,
            fontSize = 14.sp
        )

        if (isPlaceholderVisible) {

            // Плейсхолдер
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.error_nothing),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.playlists_is_empty),
                    textAlign = TextAlign.Center,
                    fontSize = 19.sp,
                    fontFamily = FontFamily(Font(R.font.ys_display_medium)),
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

        } else {

            // вместо androidx.recyclerview.widget.GridLayoutManager
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {

                items(
                    items = playlists,
                    key = { it.id },              // стабильный ключ, чтобы не перерисовывать все items, а только изменившиеся
                    contentType = { "playlist" } // тип элемента для ускоренного отображения
                ) { playlist ->

                    PlaylistItem(
                        playlist = playlist,
                        onClick = onPlaylistClick
                    )

                }
            }
        }
    }
}