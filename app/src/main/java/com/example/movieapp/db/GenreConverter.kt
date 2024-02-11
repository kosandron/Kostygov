package com.example.movieapp.db

import androidx.room.TypeConverter
import com.example.movieapp.models.Country
import com.example.movieapp.models.Genre

class GenreConverter {
    @TypeConverter
    fun fromGenre(genreList: List<Genre>): String {
        return genreList.joinToString(", ") { it.genre }
    }

    @TypeConverter
    fun toGenre(genreString: String): List<Genre> {
        return genreString.split(", ").map { Genre(it) }
    }
}