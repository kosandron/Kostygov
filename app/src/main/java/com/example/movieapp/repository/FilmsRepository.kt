package com.example.movieapp.repository

import com.example.movieapp.api.RetrofitInstance
import com.example.movieapp.db.FilmDataBase
import com.example.movieapp.models.Film

class FilmsRepository(val db: FilmDataBase) {
    suspend fun getTopFilms(pageNumber: Int, type: String = "TOP_100_POPULAR_FILMS") =
        RetrofitInstance.api.getFilmsCollection(type, pageNumber)

    suspend fun searchFilms(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForFilms(searchQuery, pageNumber)

    suspend fun getFilmById(id: Int) = RetrofitInstance.api.getFilmById(id)

    suspend fun upsert(film: Film) = db.getFilmDao().upsert(film)

    fun getSavedFilms() = db.getFilmDao().getAllFilms()

    suspend fun deleteFilm(film: Film) = db.getFilmDao().deleteFilm(film)
}