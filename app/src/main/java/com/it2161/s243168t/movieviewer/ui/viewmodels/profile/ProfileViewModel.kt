package com.it2161.s243168t.movieviewer.ui.viewmodels.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it2161.s243168t.movieviewer.data.SessionManager
import com.it2161.s243168t.movieviewer.data.repositories.UserRepository
import com.it2161.s243168t.movieviewer.utils.Validators
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ProfileUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = sessionManager.userId.first()
            if (userId != null) {
                userRepository.getUserByIdFlow(userId.toInt()).collect { user ->
                    _uiState.update {
                        it.copy(
                            user = user,
                            userId = user?.userId ?: "",
                            preferredName = user?.preferredName ?: "",
                            dob = user?.dateOfBirth?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it) } ?: "",
                            profilePicPath = user?.profilePicture,
                            isLoading = false
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.OnEditToggle -> {
                val isEditing = !_uiState.value.isEditMode
                _uiState.update { it.copy(isEditMode = isEditing) }
            }
            is ProfileUiEvent.OnUserIdChanged -> _uiState.update { it.copy(userId = event.value) }
            is ProfileUiEvent.OnNameChanged -> _uiState.update { it.copy(preferredName = event.value) }
            is ProfileUiEvent.OnDobChanged -> _uiState.update { it.copy(dob = event.value) }
            is ProfileUiEvent.OnProfilePicChanged -> _uiState.update { it.copy(profilePicPath = event.value) }
            is ProfileUiEvent.OnNewPasswordChanged -> _uiState.update { it.copy(newPassword = event.value) }
            is ProfileUiEvent.OnConfirmNewPasswordChanged -> _uiState.update { it.copy(confirmNewPassword = event.value) }
            ProfileUiEvent.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            ProfileUiEvent.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            ProfileUiEvent.OnSaveClicked -> saveProfileChanges()
        }
    }

    private fun saveProfileChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value

            if (state.newPassword.isNotEmpty() && !Validators.doPasswordsMatch(state.newPassword, state.confirmNewPassword)) {
                emitEffect(ProfileUiEffect.ShowSnackbar("Passwords do not match"))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            val dobDate: Date
            try {
                dobDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(state.dob)
            } catch (e: Exception) {
                emitEffect(ProfileUiEffect.ShowSnackbar("Invalid date format. Please use dd/MM/yyyy."))
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }

            state.user?.let {
                val updatedUser = it.copy(
                    userId = state.userId,
                    preferredName = state.preferredName,
                    dateOfBirth = dobDate,
                    profilePicture = state.profilePicPath,
                    password = if (state.newPassword.isNotEmpty()) state.newPassword else it.password
                )
                userRepository.updateUser(updatedUser)
                emitEffect(ProfileUiEffect.ShowSnackbar("Profile updated successfully"))
                _uiState.update { it.copy(isLoading = false, isEditMode = false) }
            }
        }
    }

    private suspend fun emitEffect(effect: ProfileUiEffect) {
        _uiEffect.emit(effect)
    }
}
