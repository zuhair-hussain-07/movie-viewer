package com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail

sealed class MovieDetailUiEffect {
    data class ShowToast(val message: String) : MovieDetailUiEffect()
    object NavigateBack : MovieDetailUiEffect()
}
