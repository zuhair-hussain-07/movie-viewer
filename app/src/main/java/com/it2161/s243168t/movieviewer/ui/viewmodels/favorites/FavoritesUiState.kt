package com.it2161.s243168t.movieviewer.ui.viewmodels.favorites

import com.it2161.s243168t.movieviewer.data.local.models.Movie

data class FavoritesUiState(
    val movies: List<Movie> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favoriteIds: Set<Int> = emptySet()
)
