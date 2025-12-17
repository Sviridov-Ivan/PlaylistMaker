package com.example.playlistmaker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE) // для добавления трека в таблицу с избранными треками
    suspend fun insertTracks(track: TrackEntity) // suspend обозначает асинхронный характер метода, то есть он может выполняться в фоновом потоке

    @Query("DELETE FROM track_table WHERE trackId = :id")
    suspend fun deleteTrackById(id: Long) // для удаления трека из таблицы избранных треков по id (нет лишней аллокации памяти быстрее работает)

    @Query("SELECT * FROM track_table ORDER BY addedAt DESC") // для получения списка со всеми треками сразу с определением порядка
    //suspend fun getAllTracks(): List<TrackEntity> // bозвращает список объектов TrackEntity из таблицы movie_table по запросу SELECT * FROM track_table
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun getFavoriteTrackIds(): List<Long> // для получения списка идентификаторов всех треков, которые добавлены в избранное

}