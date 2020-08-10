package com.test.movies.repo


import com.test.movies.utils.Constants
import com.test.movies.model.MovieModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiInterface {
    @GET("movies.json")
    fun getMovies(): Call<ArrayList<MovieModel>>

    companion object Factory {

        fun create(): ApiInterface {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()

            return retrofit.create(ApiInterface::class.java)

        }

    }

}