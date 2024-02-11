package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.models.Film
import com.example.movieapp.ui.FilmActivity
import com.example.movieapp.ui.FilmsViewModel
import com.example.movieapp.utils.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_film.*

class FilmFragment : Fragment(R.layout.fragment_film) {
    lateinit var viewModel: FilmsViewModel
    var film: Film? = null
    val args: FilmFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmActivity).viewModel

        viewModel.getFilmById(args.film.filmId ?: args.film.kinopoiskId!!)
        viewModel.filmById.observe(viewLifecycleOwner, Observer { response ->
             when(response) {
                is Resource.Success -> {
                    response.data?.let { filmResponse ->
                        film = filmResponse
                        Glide.with(this)
                            .load(filmResponse.posterUrl)
                            .centerCrop()
                            .into(movie_image)
                        tvFilmName.text = filmResponse?.nameEn ?: filmResponse?.nameRu
                        tvGenre.text = getString(R.string.genres) + filmResponse.genres.toString().substring(1, filmResponse.genres.toString().length - 1)
                        tvLand.text = getString(R.string.lands) + filmResponse.countries.toString().substring(1, filmResponse.countries.toString().length - 1)
                        tvDescription.text = getString(R.string.description) + filmResponse.description
                    }
                }
                is Resource.Error -> {
                    film = args.film
                    Glide.with(this)
                        .load(film?.posterUrl)
                        .centerCrop()
                        .into(movie_image)
                    tvFilmName.text = film?.nameEn ?: film?.nameRu
                    tvGenre.text = getString(R.string.genres) + film?.genres.toString().substring(1, film?.genres.toString().length - 1)
                    tvLand.text = getString(R.string.lands) + film?.countries.toString().substring(1, film?.countries.toString().length - 1)
                    tvDescription.text = getString(R.string.description) + film?.description
                }
                is Resource.Loading -> {
                    film = args.film
                    Glide.with(this)
                        .load(film?.posterUrl)
                        .centerCrop()
                        .into(movie_image)
                    tvFilmName.text = film?.nameEn ?: film?.nameRu
                    tvGenre.text = getString(R.string.genres) + film?.genres.toString().substring(1, film?.genres.toString().length - 1)
                    tvLand.text = getString(R.string.lands) + film?.countries.toString().substring(1, film?.countries.toString().length - 1)
                    tvDescription.text = getString(R.string.description) + film?.description
                }
            }

        })
        if (film == null) {
            film = args.film
        }

        fab.setOnClickListener {
            viewModel.saveFilm(film!!)
            Snackbar.make(view, "Film saved successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}