package com.example.playlistmaker.media.ui.compose


import android.content.res.Configuration
import com.example.playlistmaker.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.ui.PlaylistMakerTheme
import com.example.playlistmaker.media.domain.model.Playlist

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: (Playlist) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(
                start = 4.dp,
                end = 4.dp,
                top = 4.dp,
                bottom = 4.dp
            )
            .clickable { onClick(playlist)}
    ) {

        AsyncImage(
            model = playlist.artworkPath,
            contentDescription = stringResource(R.string.contentDescription_arrow_back),
            placeholder = painterResource(R.drawable.placeholder_album), // заглушка
            error = painterResource(R.drawable.placeholder_album), // ошибка загрузки
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // квадрат как SquareImageView
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = playlist.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = MaterialTheme.colorScheme.onSecondary,
            style = TextStyle(
                platformStyle = PlatformTextStyle( // отключения встроенного паддинга
                    includeFontPadding = false
                )
            )
        )

        Text(
            text = pluralStringResource(R.plurals.playlist_tracks_count, playlist.trackCount, playlist.trackCount), // множественное число для треков
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.ys_display_regular)),
            color = MaterialTheme.colorScheme.onSecondary,
            style = TextStyle(
                platformStyle = PlatformTextStyle( // отключения встроенного паддинга
                    includeFontPadding = false
                )
            )
        )
    }
}

@Preview(name = "Light Theme", showBackground = true)
@Preview(name = "Dark Theme", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PlaylistItemPreview() {

    val playlist = Playlist(
        id = 112132,
        name = "Sashenka",
        description = "Krasapchic",
        artworkPath = "",
        trackIds = listOf("12,211,121"),
        trackCount = 12
    )

    PlaylistMakerTheme {
        PlaylistItem(
            playlist = playlist,
            onClick = {}
        )
    }
}