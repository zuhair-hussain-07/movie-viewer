package com.it2161.s243168t.movieviewer.ui.viewmodels.movielist

import com.it2161.s243168t.movieviewer.data.local.models.Movie

sealed class MovieUiEvent {
    data class OnCategoryChanged(val category: String) : MovieUiEvent()
    data class OnSearchQueryChanged(val query: String) : MovieUiEvent()
    data class OnMovieClicked(val movieId: Int) : MovieUiEvent()
    data class ToggleFavorite(val movie: Movie) : MovieUiEvent()
    object RefreshList : MovieUiEvent()
    object OnSearchDisabledClicked : MovieUiEvent()
}
