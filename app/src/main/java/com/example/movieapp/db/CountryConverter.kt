package com.example.movieapp.db

import androidx.room.TypeConverter
import com.example.movieapp.models.Country

class CountryConverter {
    @TypeConverter
    fun fromCountry(countryList: List<Country>): String {
        return countryList.joinToString(", ") { it.country }
    }

    @TypeConverter
    fun toCountry(countryString: String): List<Country> {
        return countryString.split(", ").map { Country(it) }
    }
}