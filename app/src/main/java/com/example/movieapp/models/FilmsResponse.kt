package com.example.movieapp.models

data class FilmsResponse(
    val pagesCount: Int,
    val keyword: String,
    val type: Int,
    val films: MutableList<Film>?,
    val items: MutableList<Film>?
)