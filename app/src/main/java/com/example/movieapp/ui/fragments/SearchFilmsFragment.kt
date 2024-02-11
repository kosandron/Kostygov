package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapters.FilmsAdapter
import com.example.movieapp.ui.FilmActivity
import com.example.movieapp.ui.FilmsViewModel
import com.example.movieapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.movieapp.utils.Constants.Companion.SEARCH_FILMS_TIME_DELAY
import com.example.movieapp.utils.Resource
import kotlinx.android.synthetic.main.fragment_search_films.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFilmsFragment : Fragment(R.layout.fragment_search_films) {
    lateinit var viewModel: FilmsViewModel
    lateinit var filmsAdapter: FilmsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmActivity).viewModel
        setupRecyclerView()

        filmsAdapter.setOnItemClickListener { film ->
            val bundle = Bundle().apply {
                putSerializable("film", film)
            }
            findNavController().navigate(
                R.id.action_searchFilmsFragment_to_filmFragment,
                bundle
            )
        }

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_FILMS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchFilms(editable.toString())
                    }
                }
            }
        }

        viewModel.searchFilms.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { filmsResponse ->
                        filmsAdapter.differ.submitList(filmsResponse.films?.toList())
                        val totalPages = filmsResponse.pagesCount
                        isLastPage = viewModel.searchFilmsPage == totalPages
                        if (isLastPage) {
                            rvSearchFilms.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured:  $message", Toast.LENGTH_LONG).show()
                    }
                    response.data?.let {  filmsResponse ->
                        filmsAdapter.differ.submitList(filmsResponse.films)
                    }
                }
                is Resource.Loading -> showProgressBar()
            }

        })
    }


    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBegginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBegginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.searchFilms(etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        filmsAdapter = FilmsAdapter()
        rvSearchFilms.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFilmsFragment.scrollListener)
        }
    }
}