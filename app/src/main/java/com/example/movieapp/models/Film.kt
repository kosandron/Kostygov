package com.example.movieapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.movieapp.db.CountryConverter
import com.example.movieapp.db.GenreConverter
import java.io.Serializable

@Entity(tableName = "films")
data class Film(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @TypeConverters(CountryConverter::class) val countries: List<Country>,
    @TypeConverters(GenreConverter::class) val genres: List<Genre>,
    val imdbId: String?,
    val kinopoiskId: Int?,
    val nameEn: String?,
    val nameOriginal: String?,
    val nameRu: String?,
    val posterUrl: String?,
    val posterUrlPreview: String?,
    val ratingImdb: Double?,
    val ratingKinopoisk: Double?,
    val type: String?,
    val year: Int?
) : Serializable