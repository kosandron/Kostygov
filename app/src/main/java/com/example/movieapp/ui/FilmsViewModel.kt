package com.example.movieapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.movieapp.FilmsApplication
import com.example.movieapp.models.Film
import com.example.movieapp.models.FilmsResponse
import com.example.movieapp.repository.FilmsRepository
import com.example.movieapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class FilmsViewModel(
    app: Application,
    val filmsRepository: FilmsRepository
    ) : AndroidViewModel(app) {
        val topFilms: MutableLiveData<Resource<FilmsResponse>> = MutableLiveData()
        var topFilmsPage = 1
        var topFilmsResponse: FilmsResponse? = null

        val searchFilms: MutableLiveData<Resource<FilmsResponse>> = MutableLiveData()
        var searchFilmsPage = 1
        var searchFilmsResponse: FilmsResponse? = null
        var newSearchQuery:String? = null
        var oldSearchQuery:String? = null

        init {
            Log.d("ViewModel", "Start Call!")
            getFilmsCollection("TOP_100_POPULAR_FILMS")
            Log.d("ViewModel", "End Call!")
        }

        fun getFilmsCollection(filmCollection: String = "TOP_100_POPULAR_FILMS") = viewModelScope.launch {
            safeBreakingCall(filmCollection)
        }

        fun searchFilms(searchQuery: String) = viewModelScope.launch {
            safeSearchCall(searchQuery)
        }

        private fun handleTopFilmsResponse(response: Response<FilmsResponse>) : Resource<FilmsResponse> {
            if (response.isSuccessful) {
                Log.d("Answer", response.body()?.toString() ?: "Empty")
                response.body()?.let {  resultResponse ->
                    topFilmsPage++
                    Log.d("Answer", response.body()?.toString() ?: "Empty")
                    if (topFilmsResponse == null) {
                        topFilmsResponse = resultResponse
                    } else {
                        val oldFilms = topFilmsResponse?.films
                        val newFilms = resultResponse.films
                        newFilms?.let {
                            oldFilms?.addAll(newFilms)
                        }

                    }
                    return Resource.Success(topFilmsResponse ?: resultResponse)
                }
            }
            return Resource.Error(response.message())
        }

        private fun handleSearchFilmsResponse(response: Response<FilmsResponse>) : Resource<FilmsResponse> {
            if (response.isSuccessful) {
                Log.d("Answer find body: ", response.body()?.toString() ?: "Empty")
                response.body()?.let {  resultResponse ->
                    searchFilmsPage++
                    if(searchFilmsResponse == null || newSearchQuery != oldSearchQuery) {
                        searchFilmsPage = 1
                        oldSearchQuery = newSearchQuery
                        searchFilmsResponse = resultResponse
                    } else {
                        val oldFilms = searchFilmsResponse?.films
                        val newFilms = resultResponse.films
                        newFilms?.let {
                            oldFilms?.addAll(newFilms)
                        }
                    }
                    return Resource.Success(searchFilmsResponse ?: resultResponse)
                }
            }
            return Resource.Error(response.message())
        }

        fun saveFilm(film: Film) = viewModelScope.launch {
            filmsRepository.upsert(film)
        }

        fun getSavedFilms() = filmsRepository.getSavedFilms()

        fun deleteFilm(film: Film) = viewModelScope.launch {
            filmsRepository.deleteFilm(film)
        }

        private suspend fun safeBreakingCall(filmCollection: String = "TOP_100_POPULAR_FILMS") {
            topFilms.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    Log.d("ViewModel", "Response start!")
                    val response = filmsRepository.getTopFilms(topFilmsPage, filmCollection)
                    Log.d("Answer", response.body()?.toString() ?: "Empty")
                    topFilms.postValue(handleTopFilmsResponse(response))
                } else {
                    topFilms.postValue((Resource.Error("No internet connection")))
                }
            } catch (t: Throwable) {
                when(t) {
                    is IOException -> topFilms.postValue(Resource.Error("Network Failure"))
                    else -> topFilms.postValue(Resource.Error("Conversion Error"))
                }
            }
        }

        private suspend fun safeSearchCall(searchQuery: String) {
            newSearchQuery = searchQuery
            searchFilms.postValue(Resource.Loading())
            try {
                if (hasInternetConnection()) {
                    val response = filmsRepository.searchFilms(searchQuery, searchFilmsPage)
                    searchFilms.postValue(handleSearchFilmsResponse(response))
                } else {
                    searchFilms.postValue((Resource.Error("No internet connection")))
                }
            } catch (t: Throwable) {
                when(t) {
                    is IOException -> searchFilms.postValue(Resource.Error("Network Failure"))
                    else -> searchFilms.postValue(Resource.Error("Conversion Error"))
                }
            }
        }

        private fun hasInternetConnection(): Boolean {
            val connectivityManager = getApplication<FilmsApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                connectivityManager.activeNetworkInfo?.run {
                    return when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }

            return false
        }
}