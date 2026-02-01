package com.it2161.s243168t.movieviewer.ui.viewmodels.movielist

import com.it2161.s243168t.movieviewer.data.local.models.Movie

data class MovieUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val selectedCategory: String = "popular",
    val searchQuery: String = "",
    val isOnline: Boolean = true,
    val favoriteIds: Set<Int> = emptySet()
)
