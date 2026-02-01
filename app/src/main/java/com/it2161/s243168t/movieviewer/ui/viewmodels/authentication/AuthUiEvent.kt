package com.it2161.s243168t.movieviewer.ui.viewmodels.authentication

sealed class AuthUiEvent {
    data class OnUserIdChanged(val value: String) : AuthUiEvent()
    data class OnPasswordChanged(val value: String) : AuthUiEvent()
    data class OnConfirmPasswordChanged(val value: String) : AuthUiEvent()
    data class OnDobChanged(val value: String) : AuthUiEvent()
    data class OnPreferredNameChanged(val value: String) : AuthUiEvent()
    data class OnProfilePicPathChanged(val value: String) : AuthUiEvent()
    object TogglePasswordVisibility : AuthUiEvent()
    object ToggleConfirmPasswordVisibility : AuthUiEvent()
    object OnLoginClicked : AuthUiEvent()
    object OnRegisterClicked : AuthUiEvent()
    object OnLogoutClicked : AuthUiEvent()
}
