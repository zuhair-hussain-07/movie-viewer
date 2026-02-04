package com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail

sealed class MovieDetailUiEffect {
    data class ShowSnackbar(val message: String) : MovieDetailUiEffect()
    object NavigateBack : MovieDetailUiEffect()
}
