package com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail

sealed class MovieDetailUiEvent {
    data class LoadMovieDetail(val movieId: Int) : MovieDetailUiEvent()
    data class ToggleFavorite(val movieId: Int) : MovieDetailUiEvent()
    object OnBackClicked : MovieDetailUiEvent()
}
