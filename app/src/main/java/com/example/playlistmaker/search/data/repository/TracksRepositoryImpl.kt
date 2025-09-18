package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.data.dto.TrackDTO
import com.example.playlistmaker.search.data.dto.TracksResponseDTO
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.ITunesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TracksRepositoryImpl(api: ITunesApi) : TracksRepository { // параметр в конструктор (api: ITunesApi) добавил АС
    override fun searchTracks(
        query: String,
        onSuccess: (List<Track>) -> Unit,
        onError: () -> Unit
    ) {
        ITunesService.api.search(query).enqueue(object : Callback<TracksResponseDTO> {
            override fun onResponse(
                call: Call<TracksResponseDTO>,
                response: Response<TracksResponseDTO>
            ) {
                if (response.isSuccessful) { // Сервер ответил 200
                    val trackList = response.body()?.results?.map { it.toDomain() } ?: emptyList()

                    onSuccess(trackList)
                } else {
                    onError() // Сервер ответил 404 / 500 → ошибка
                }
            }

            override fun onFailure(
                call: Call<TracksResponseDTO>,
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