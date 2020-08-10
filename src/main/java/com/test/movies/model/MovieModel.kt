package com.test.movies.model

import com.google.gson.annotations.SerializedName

class MovieModel {

    @SerializedName("title")
    var title: String? = null
    @SerializedName("image")
    var image: String? = null
    @SerializedName("rating")
    var rating: Float? = null
    @SerializedName("releaseYear")
    var releaseYear: Int? = null
    @SerializedName("genre")
    var genre: List<String>? = null

}
