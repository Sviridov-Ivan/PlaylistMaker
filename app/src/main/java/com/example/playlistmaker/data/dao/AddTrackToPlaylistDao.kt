package com.example.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.entity.AddTrackToPlaylistEntity


@Dao
interface AddTrackToPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // для добавления трека в таблицу с избранными треками
    suspend fun addTrack(track: AddTrackToPlaylistEntity) // suspend обозначает асинхронный характер метода, то есть он может выполняться в фоновом потоке
}