package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.ui.FilmActivity
import com.example.movieapp.ui.FilmsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_film.*

class FilmFragment : Fragment(R.layout.fragment_film) {
    lateinit var viewModel: FilmsViewModel
    val args: FilmFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmActivity).viewModel

        val film = args.film
        Glide.with(this)
            .load(film.posterUrl)
            .centerCrop()
            .into(movie_image)
        tvFilmName.text = film.nameEn ?: film.nameRu
        tvGenre.text = film.genres.toString()
        tvLand.text = film.countries.toString()

        fab.setOnClickListener {
            viewModel.saveFilm(film)
            Snackbar.make(view, "Film saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}