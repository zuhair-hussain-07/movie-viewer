package com.it2161.s243168t.movieviewer.ui.viewmodels.authentication

sealed class AuthUiEffect {
    data class Navigate(val route: String) : AuthUiEffect()
    data class ShowToast(val message: String) : AuthUiEffect()
}
