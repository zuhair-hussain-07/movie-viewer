package com.it2161.s243168t.movieviewer.ui.viewmodels.profile

sealed class ProfileUiEvent {
    object OnEditToggle : ProfileUiEvent()
    data class OnUserIdChanged(val value: String) : ProfileUiEvent()
    data class OnNameChanged(val value: String) : ProfileUiEvent()
    data class OnDobChanged(val value: String) : ProfileUiEvent()
    data class OnProfilePicChanged(val value: String) : ProfileUiEvent()
    data class OnNewPasswordChanged(val value: String) : ProfileUiEvent()
    data class OnConfirmNewPasswordChanged(val value: String) : ProfileUiEvent()
    object TogglePasswordVisibility : ProfileUiEvent()
    object ToggleConfirmPasswordVisibility : ProfileUiEvent()
    object OnSaveClicked : ProfileUiEvent()
}
