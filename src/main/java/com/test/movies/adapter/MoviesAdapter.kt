package com.test.movies.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.test.movies.model.MovieModel
import com.test.movies.R
import java.util.Locale
import kotlin.collections.ArrayList

/*
* The Adapter binds view holder for RecyclerView, Displayes all data from Api response
* Filter option is enabled to return list as per user Search Query
 */
class MoviesAdapter(ctx: Context, private var movieList: ArrayList<MovieModel>) :
    RecyclerView.Adapter<MoviesViewHolder>(), Filterable {

    private var context = ctx
    var movieFilterList = movieList

    init {
        movieFilterList = movieList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MoviesViewHolder(context, inflater, parent)
    }

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val movie: MovieModel = movieFilterList[position]
        holder.bind(movie)
        if (position % 2 == 1) {
            holder.itemView.setBackgroundColor(context.resources.getColor(R.color.colorGrey))
        } else {
            holder.itemView.setBackgroundColor(context.resources.getColor(android.R.color.white))
        }
    }

    override fun getItemCount(): Int = movieFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                movieFilterList = if (charSearch.isEmpty()) {
                    movieList
                } else {

                    val resultList = ArrayList<MovieModel>()
                    for (movie in movieList) {
                        if (movie.title?.toLowerCase(Locale.ROOT)
                                ?.contains(charSearch.toLowerCase(Locale.ROOT))!!
                        ) {
                            resultList.add(movie)
                        }
                    }
                    resultList

                }
                val filterResults = FilterResults()
                filterResults.values = movieFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                movieFilterList = results?.values as ArrayList<MovieModel>
                notifyDataSetChanged()
            }

        }
    }

}