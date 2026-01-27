package com.it2161.s243168t.movieviewer.ui.viewmodels.authentication

data class AuthUiState(
    val isLoading: Boolean = false,
    val userId: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val dob: String = "",
    val preferredName: String = "",
    val profilePicPath: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)
