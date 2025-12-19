package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.dao.AddTrackToPlaylistDao
import com.example.playlistmaker.data.dao.PlaylistDao
import com.example.playlistmaker.data.dao.TrackDao
import com.example.playlistmaker.data.entity.AddTrackToPlaylistEntity
import com.example.playlistmaker.data.entity.PlaylistEntity
import com.example.playlistmaker.data.entity.TrackEntity

@Database(version = 4, entities = [
    TrackEntity::class,
    PlaylistEntity::class,
    AddTrackToPlaylistEntity::class]) // version указывает версию базы данных, которая должна быть использована при создании экземпляра класса AppDatabase. Параметр entities определяет список классов сущностей, которые должны быть зарегистрированы в базе данных
abstract class AppDatabase : RoomDatabase() { // наследуется от интерфейса RoomDatabase

    abstract fun trackDao(): TrackDao // возвращает объект TrackDao — интерфейс для работы с сущностями таблицы track_table в приложении
    abstract fun playlistDao(): PlaylistDao // возвращает объект PlaylistDao — интерфейс для работы с сущностями таблицы playlist_table в приложении

    abstract fun addTrackToPlaylistDao(): AddTrackToPlaylistDao // возвращает объект AddTrackToPlaylistDao — интерфейс для работы с сущностями таблицы playlist_tracks_table в приложении
}