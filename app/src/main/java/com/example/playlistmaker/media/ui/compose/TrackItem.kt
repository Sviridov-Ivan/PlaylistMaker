package com.example.playlistmaker.media.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.formatDuration

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .clickable{ onClick() }
            .padding(start = 13.dp, end = 12.dp, top = 8.dp, bottom = 8.dp), // padding_act_track_*
        verticalAlignment = Alignment.CenterVertically
    ) {
        // для загрузки картинок с использованием coil
        AsyncImage(
            model = track.artworkUrl100,
            contentDescription = track.trackName,
            placeholder = painterResource(R.drawable.placeholder_album), // заглушка
            error = painterResource(R.drawable.placeholder_album), // ошибка загрузки
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(2.dp)), // padding_act_track_main_icon_size
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .height(45.dp) // размер вровень с картинкой для откладывания паддинга
                .padding(start = 8.dp, top = 4.dp, bottom = 2.dp, end = 8.dp) // margin_act_track_top_bottom
        ) {
            Text(
                text = track.trackName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primaryContainer, // colorForTrackName
                style = TextStyle(
                    platformStyle = PlatformTextStyle( // отключения встроенного паддинга
                        includeFontPadding = false
                    )
                )
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontSize = 11.sp, // artist_name_act_track_text_size
                    color = MaterialTheme.colorScheme.tertiary, // colorForSettingsIcons
                    style = TextStyle(
                        platformStyle = PlatformTextStyle( // отключения встроенного паддинга
                            includeFontPadding = false
                        )
                    )
                )
                //Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_dot_artist_time),
                    contentDescription = null,
                    modifier = Modifier.size(13.dp), // icon_dot_size_act_track_size
                    tint = MaterialTheme.colorScheme.tertiary // colorForSettingsIcons
                )
                //Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatDuration(track.trackTimeMillis),
                    fontFamily = FontFamily(Font(R.font.ys_display_regular)),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary, // colorForSettingsIcons
                    style = TextStyle(
                        platformStyle = PlatformTextStyle( // отключения встроенного паддинга
                            includeFontPadding = false
                        )
                    )
                )
            }
        }

        Icon(
            painter = painterResource(R.drawable.light_mode_6),
            contentDescription = null,
            modifier = Modifier.size(24.dp), // ImageViewSettingsStyle
            tint = MaterialTheme.colorScheme.tertiary // colorForSettingsIcons
        )
    }
}

@Preview(name = "Light Theme", showBackground = true)
@Preview(name = "Dark Theme", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TrackItemPreview() {

    val track = Track(
        trackId = 1,
        trackName = "Smells Like Teen Spirit",
        artistName = "Nirvana",
        trackTimeMillis = 301000,
        artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/21/64/87/21648735-d4d2-6d9a-8d4a-8d5f7e88c96e/source/100x100bb.jpg",
        previewUrl = "",
        collectionName = "Vanyshka",
        releaseDate = "2025",
        primaryGenreName = "Rock",
        country = "Russia",
    )

    PlaylistMakerTheme {
        TrackItem(
            track = track,
            onClick = {}
        )
    }
}
