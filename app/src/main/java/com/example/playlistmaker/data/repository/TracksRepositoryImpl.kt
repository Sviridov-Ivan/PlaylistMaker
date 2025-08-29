package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackDTO
import com.example.playlistmaker.data.network.ITunesApi
import com.example.playlistmaker.data.network.ITunesService
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(api: ITunesApi) : TracksRepository { // параметр в конструктор (api: ITunesApi) добавил АС
    override fun searchTracks(
        query: String,
        onSuccess: (List<Track>) -> Unit,
        onError: () -> Unit
    ) {
        ITunesService.api.search(query).enqueue(object : Callback<com.example.playlistmaker.data.dto.TracksResponseDTO> {
            override fun onResponse(
                call: Call<com.example.playlistmaker.data.dto.TracksResponseDTO>,
                response: Response<com.example.playlistmaker.data.dto.TracksResponseDTO>
            ) {
                if (response.isSuccessful) { // Сервер ответил 200
                    val trackList = response.body()?.results?.map { it.toDomain() } ?: emptyList()

                    onSuccess(trackList)
                } else {
                    onError() // Сервер ответил 404 / 500 → ошибка
                }
            }

            override fun onFailure(
                call: Call<com.example.playlistmaker.data.dto.TracksResponseDTO>,
                t: Throwable
            ) {
                onError()
            }
        })
    }

    private fun TrackDTO.toDomain() = Track(
        trackId = trackId,
        trackName = trackName,
        artistName = artistName,
        trackTimeMillis = trackTimeMillis,//formatDuration(trackTimeMillis),
        artworkUrl100 = artworkUrl100,
        previewUrl = previewUrl,
        collectionName = collectionName,
        releaseDate = releaseDate,
        primaryGenreName = primaryGenreName,
        country = country
    )
}