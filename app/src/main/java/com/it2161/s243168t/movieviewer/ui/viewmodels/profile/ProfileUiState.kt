package com.it2161.s243168t.movieviewer.ui.viewmodels.profile

import com.it2161.s243168t.movieviewer.data.local.models.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isEditMode: Boolean = false,
    val userId: String = "",
    val preferredName: String = "",
    val dob: String = "",
    val profilePicPath: String? = null,
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)
