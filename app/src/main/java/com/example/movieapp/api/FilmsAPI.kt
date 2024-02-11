package com.example.movieapp.api

import com.example.movieapp.models.FilmsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FilmsAPI {
    @GET("v2.2/films/top")
    suspend fun getFilmsCollection(
        @Query("type") type: String = "TOP_250_BEST_FILMS",
        @Query("page") page: Int = 1
    ): Response<FilmsResponse>

    @GET("v2.1/films/search-by-keyword")
    suspend fun searchForFilms(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1
    ): Response<FilmsResponse>
}