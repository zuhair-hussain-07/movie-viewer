package com.it2161.s243168t.movieviewer.ui.viewmodels.favorites

sealed class FavoritesUiEffect {
    data class ShowSnackbar(val message: String) : FavoritesUiEffect()
    data class NavigateToDetail(val movieId: Int) : FavoritesUiEffect()
}
