package com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail

import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.data.local.models.Review

data class MovieDetailUiState(
    val movie: Movie? = null,
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOnline: Boolean = true
)
