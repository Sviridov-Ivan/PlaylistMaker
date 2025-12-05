package com.example.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.search.data.db.dao.TrackDao
import com.example.playlistmaker.search.data.db.entity.TrackEntity

@Database(version = 2, entities = [TrackEntity::class]) // version указывает версию базы данных, которая должна быть использована при создании экземпляра класса AppDatabase. Параметр entities определяет список классов сущностей, которые должны быть зарегистрированы в базе данных
abstract class AppDatabase : RoomDatabase() { // наследуется от интерфейса RoomDatabase

    abstract fun trackDao(): TrackDao // возвращает объект TrackDao — интерфейс для работы с сущностями таблицы track_table в приложении
}