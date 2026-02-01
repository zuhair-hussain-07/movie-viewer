package com.it2161.s243168t.movieviewer.ui.viewmodels.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.SessionManager
import com.it2161.s243168t.movieviewer.data.local.models.User
import com.it2161.s243168t.movieviewer.data.repositories.AuthRepository
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<AuthUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.OnUserIdChanged -> _uiState.update { it.copy(userId = event.value) }
            is AuthUiEvent.OnPasswordChanged -> _uiState.update { it.copy(password = event.value) }
            is AuthUiEvent.OnConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = event.value) }
            is AuthUiEvent.OnDobChanged -> _uiState.update { it.copy(dob = event.value) }
            is AuthUiEvent.OnPreferredNameChanged -> _uiState.update { it.copy(preferredName = event.value) }
            is AuthUiEvent.OnProfilePicPathChanged -> _uiState.update { it.copy(profilePicPath = event.value) }
            AuthUiEvent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            AuthUiEvent.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            AuthUiEvent.OnLoginClicked -> loginUser()
            AuthUiEvent.OnRegisterClicked -> registerUser()
            AuthUiEvent.OnLogoutClicked -> logoutUser()
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value

            if (state.userId.isBlank() || state.password.isBlank() || state.dob.isBlank() || state.preferredName.isBlank()) {
                emitEffect(AuthUiEffect.ShowSnackbar("All fields except profile picture are required."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            if (!Validators.doPasswordsMatch(state.password, state.confirmPassword)) {
                emitEffect(AuthUiEffect.ShowSnackbar("Passwords do not match"))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val dobDate: Date
            try {
                // Assuming the date format from the UI is dd/MM/yyyy
                dobDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(state.dob)
            } catch (e: Exception) {
                emitEffect(AuthUiEffect.ShowSnackbar("Invalid date format. Please use dd/MM/yyyy."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            if (!Validators.isDateOfBirthInPast(dobDate.time)) {
                emitEffect(AuthUiEffect.ShowSnackbar("Date of birth must be in the past."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val user = User(
                userId = state.userId,
                password = state.password,
                dateOfBirth = dobDate,
                preferredName = state.preferredName,
                profilePicture = state.profilePicPath
            )

            val registered = authRepository.register(user)
            if (registered) {
                val loggedIn = authRepository.login(user.userId, user.password)
                if (loggedIn) {
                    emitEffect(AuthUiEffect.Navigate(Routes.MovieList.route))
                } else {
                    emitEffect(AuthUiEffect.ShowSnackbar("Registration successful, but auto-login failed. Please log in manually."))
                    emitEffect(AuthUiEffect.Navigate(Routes.Login.route)) // Navigate to login screen
                }
            } else {
                emitEffect(AuthUiEffect.ShowSnackbar("Registration failed. User ID may already exist."))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loginUser() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            if (state.userId.isBlank() || state.password.isBlank()) {
                emitEffect(AuthUiEffect.ShowSnackbar("User ID and password cannot be empty."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            val success = authRepository.login(state.userId, state.password)

            if (success) {
                emitEffect(AuthUiEffect.Navigate(Routes.MovieList.route))
            } else {
                emitEffect(AuthUiEffect.ShowSnackbar("Invalid credentials"))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun logoutUser() {
        viewModelScope.launch {
            // Clear the session first
            sessionManager.clearSession()
            // Reset the UI state
            _uiState.value = AuthUiState()
            // Navigate to login
            emitEffect(AuthUiEffect.Navigate(Routes.Login.route))
        }
    }

    private suspend fun emitEffect(effect: AuthUiEffect) {
        _uiEffect.emit(effect)
    }
}
