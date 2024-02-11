package com.example.movieapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.repository.FilmsRepository

class FilmsViewModelProviderFactory(
    val app: Application,
    val filmsRepository: FilmsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilmsViewModel::class.java)) {
            return FilmsViewModel(app, filmsRepository) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}