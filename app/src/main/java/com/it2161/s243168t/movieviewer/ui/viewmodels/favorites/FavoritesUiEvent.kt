package com.it2161.s243168t.movieviewer.ui.viewmodels.favorites

import com.it2161.s243168t.movieviewer.data.local.models.Movie

sealed class FavoritesUiEvent {
    data class OnMovieClicked(val movieId: Int) : FavoritesUiEvent()
    data class ToggleFavorite(val movie: Movie) : FavoritesUiEvent()
    object RefreshList : FavoritesUiEvent()
}
