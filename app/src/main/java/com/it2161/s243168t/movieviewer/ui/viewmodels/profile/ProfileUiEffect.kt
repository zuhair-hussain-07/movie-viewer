package com.it2161.s243168t.movieviewer.ui.viewmodels.profile

sealed class ProfileUiEffect {
    data class ShowSnackbar(val message: String) : ProfileUiEffect()
    object NavigateBack : ProfileUiEffect()
}
