package com.example.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // новый плейлист
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE) // возможно лучше OnConflictStrategy.IGNORE
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    // обновление существующего плейлиста
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    // получение всех плейлистов
    @Query("SELECT * FROM playlist_table") // для получения списка со всеми треками сразу с определением порядка
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
}