package com.example.movieapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.movieapp.models.Film

@Database(
    entities = [Film::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(CountryConverter::class, GenreConverter::class)
abstract class FilmDataBase : RoomDatabase() {
    abstract fun getFilmDao(): FilmDao

    companion object {
        @Volatile
        private var instance: FilmDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDataBase(context).also { instance = it }
        }

        private fun createDataBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                FilmDataBase::class.java,
                "films_db.db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}