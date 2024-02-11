package com.example.movieapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.models.Film
import kotlinx.android.synthetic.main.item_film_preview.view.*

class FilmsAdapter : RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {
    inner class FilmViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_film_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(film.posterUrlPreview).into(ivFilmImage)
            tvTitle.text = film.nameEn ?: film.nameRu
            tvYear.text = film.year.toString()
            setOnClickListener {
                onItemClickListener?.let { it(film) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem.posterUrl == newItem.posterUrl
        }

        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)
    private var onItemClickListener: ((Film) -> Unit)? = null

    fun setOnItemClickListener(listener: (Film) -> Unit) {
        onItemClickListener = listener
    }
}