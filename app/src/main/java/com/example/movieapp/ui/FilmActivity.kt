package com.example.movieapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.movieapp.R
import com.example.movieapp.db.FilmDataBase
import com.example.movieapp.repository.FilmsRepository
import kotlinx.android.synthetic.main.activity_films.*

class FilmActivity : AppCompatActivity() {
    lateinit var viewModel: FilmsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_films)

        val repository = FilmsRepository(FilmDataBase(this))
        val viewModelProviderFactory = FilmsViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(FilmsViewModel::class.java)
        setContentView(R.layout.activity_films)
        bottomNavigationView.setupWithNavController(filmsNavHostFragment.findNavController())

    }
}