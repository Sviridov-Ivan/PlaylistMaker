package com.example.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.entity.AddTrackToPlaylistEntity
import com.example.playlistmaker.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AddTrackToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // для добавления трека в таблицу с избранными треками
    suspend fun addTrack(track: AddTrackToPlaylistEntity) // suspend обозначает асинхронный характер метода, то есть он может выполняться в фоновом потоке

    // получение всех треков в плейлисте
    @Query("SELECT * FROM playlist_tracks_table") // для получения списка со всеми треками сразу с определением порядка
    fun getAllTracksFromPlayList(): Flow<List<AddTrackToPlaylistEntity>>

    // удалить трек из плейлиста по id
    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}