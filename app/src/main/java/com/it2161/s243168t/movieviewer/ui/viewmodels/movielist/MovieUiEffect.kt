package com.it2161.s243168t.movieviewer.ui.viewmodels.movielist

sealed class MovieUiEffect {
    data class ShowSnackbar(val message: String) : MovieUiEffect()
    data class NavigateToDetail(val movieId: Int) : MovieUiEffect()
}
