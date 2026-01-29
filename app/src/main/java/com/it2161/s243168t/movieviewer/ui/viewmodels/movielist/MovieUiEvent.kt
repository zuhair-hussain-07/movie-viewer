package com.it2161.s243168t.movieviewer.ui.viewmodels.movielist

sealed class MovieUiEvent {
    data class OnCategoryChanged(val category: String) : MovieUiEvent()
    data class OnSearchQueryChanged(val query: String) : MovieUiEvent()
    data class OnMovieClicked(val movieId: Int) : MovieUiEvent()
    object RefreshList : MovieUiEvent()
}
