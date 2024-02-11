package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.adapters.FilmsAdapter
import com.example.movieapp.ui.FilmActivity
import com.example.movieapp.ui.FilmsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_films.*

class SavedFilmsFragment : Fragment(R.layout.fragment_saved_films) {
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
                R.id.action_savedFilmsFragment_to_filmFragment,
                bundle
            )
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val film = filmsAdapter.differ.currentList[position]
                viewModel.deleteFilm(film)
                Snackbar.make(view, "Successfully deleted film", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveFilm(film)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedFilms)
        }

        viewModel.getSavedFilms().observe(viewLifecycleOwner, Observer { films ->
            filmsAdapter.differ.submitList(films)
        })
    }

    private fun setupRecyclerView() {
        filmsAdapter = FilmsAdapter()
        rvSavedFilms.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}