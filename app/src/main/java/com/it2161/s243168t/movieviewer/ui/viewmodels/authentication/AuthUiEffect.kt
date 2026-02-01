package com.it2161.s243168t.movieviewer.ui.viewmodels.authentication

sealed class AuthUiEffect {
    data class Navigate(val route: String) : AuthUiEffect()
    data class ShowSnackbar(val message: String) : AuthUiEffect()
}
