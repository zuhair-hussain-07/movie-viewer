package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.FormFieldComponent
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.ProfilePictureComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthViewModel
import com.it2161.s243168t.movieviewer.utils.rememberCameraLauncher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    val cameraLauncher = rememberCameraLauncher {
        viewModel.onEvent(AuthUiEvent.OnProfilePicPathChanged(it.toString()))
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect {
            when (it) {
                is AuthUiEffect.Navigate -> navController.navigate(it.route)
                is AuthUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = "Create Account",
                canNavigateBack = true,
                onNavigateBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfilePictureComponent(
                uri = uiState.profilePicPath,
                isEditing = true,
                onCameraClick = cameraLauncher
            )

            FormFieldComponent(
                value = uiState.userId,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnUserIdChanged(it)) },
                label = "User ID",
                type = FormFieldType.TEXT
            )

            FormFieldComponent(
                value = uiState.preferredName,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnPreferredNameChanged(it)) },
                label = "Preferred Name",
                type = FormFieldType.TEXT
            )

            FormFieldComponent(
                value = uiState.dob,
                onValueChange = {},
                label = "Date of Birth",
                type = FormFieldType.DATE,
                onTrailingIconClick = { showDatePicker = true }
            )

            FormFieldComponent(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnPasswordChanged(it)) },
                label = "Password",
                type = FormFieldType.PASSWORD,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(AuthUiEvent.TogglePasswordVisibility) }
            )

            FormFieldComponent(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnConfirmPasswordChanged(it)) },
                label = "Confirm Password",
                type = FormFieldType.PASSWORD,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(AuthUiEvent.ToggleConfirmPasswordVisibility) }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                ButtonComponent(
                    text = "Register Now",
                    onClick = { viewModel.onEvent(AuthUiEvent.OnRegisterClicked) },
                    type = ButtonType.PRIMARY_BUTTON
                )
            }

            TextButton(onClick = { navController.navigate(Routes.Login.route) }) {
                androidx.compose.material3.Text("Already a member? Log In")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.onEvent(AuthUiEvent.OnDobChanged(sdf.format(Date(it))))
                    }
                }) {
                    androidx.compose.material3.Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
