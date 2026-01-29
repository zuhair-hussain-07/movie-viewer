package com.it2161.s243168t.movieviewer.data.remote.responses

import com.google.gson.annotations.SerializedName
import com.it2161.s243168t.movieviewer.data.remote.dtos.MovieDto

data class MovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieDto>,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null
)
