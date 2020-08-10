package com.test.movies.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.test.movies.R
import com.test.movies.model.MovieModel

/*
* This ViewHolder binds with adapter updating views with corresponding  data
 */
class MoviesViewHolder(ctx: Context, inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.activity_movie_list_item, parent, false)) {
    private var mTitleView: TextView? = null
    private var mYearView: TextView? = null
    private var mMovieView: ImageView? = null
    private var context = ctx

    init {
        mTitleView = itemView.findViewById(R.id.title)
        mYearView = itemView.findViewById(R.id.year)
        mMovieView = itemView.findViewById(R.id.movieImageView)

    }

    fun bind(movie: MovieModel) {
        loadImage(movie.image)
        mTitleView?.text = movie.title
        mYearView?.text = movie.releaseYear.toString()
    }

    private fun loadImage(image: String?) {
        if (image !== null) {
            mMovieView?.let {
                Glide.with(context)
                    .load(image)
                    .into(it)
            }
        } else {
            mMovieView?.setImageResource(R.drawable.ic_launcher_background)
        }
    }

}