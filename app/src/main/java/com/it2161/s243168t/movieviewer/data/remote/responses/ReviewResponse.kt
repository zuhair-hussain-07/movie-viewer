package com.it2161.s243168t.movieviewer.data.remote.responses

import com.google.gson.annotations.SerializedName
import com.it2161.s243168t.movieviewer.data.remote.dtos.ReviewDto

data class ReviewResponse(
    @SerializedName("id")
    val movieId: Int,
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("results")
    val results: List<ReviewDto>,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null
)
