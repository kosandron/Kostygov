package com.example.movieapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.movieapp.models.Film

@Dao
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(film: Film): Long

    @Query("SELECT * FROM films")
    fun getAllFilms(): LiveData<List<Film>>

    @Delete
    suspend fun deleteFilm(film: Film)
}