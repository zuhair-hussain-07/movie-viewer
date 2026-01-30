package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.it2161.s243168t.movieviewer.ui.components.CardComponent
import com.it2161.s243168t.movieviewer.ui.components.ConfirmationDialog
import com.it2161.s243168t.movieviewer.ui.components.FormFieldComponent
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.ProfileDetailRow
import com.it2161.s243168t.movieviewer.ui.components.ProfilePictureComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.viewmodels.profile.ProfileUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.profile.ProfileUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.profile.ProfileViewModel
import com.it2161.s243168t.movieviewer.utils.rememberCameraLauncher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    currentRoute: String = Routes.Profile.route,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val cameraLauncher = rememberCameraLauncher {
        viewModel.onEvent(ProfileUiEvent.OnProfilePicChanged(it.toString()))
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect {
            when (it) {
                is ProfileUiEffect.ShowToast -> snackbarHostState.showSnackbar(it.message)
                ProfileUiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = if (uiState.isEditMode) "Edit Profile" else "My Profile",
                canNavigateBack = uiState.isEditMode,
                onNavigateBack = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) }
            ) {
                if (!uiState.isEditMode) {
                    IconButton(onClick = { showSignOutDialog = true }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Sign Out"
                        )
                    }
                }
            }
        },
        bottomBar = {
            MovieBottomAppBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    // Handle bottom navigation
                    when (route) {
                        Routes.MovieList.route -> navController.navigate(Routes.MovieList.route)
                        Routes.Favorites.route -> navController.navigate(Routes.Favorites.route)
                        Routes.Profile.route -> {}
                    }
                }
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
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.user == null) {
                // Handle null user case - show error and redirect
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Unable to load profile",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please log in again to access your profile",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ButtonComponent(
                        text = "Back to Home",
                        onClick = { navController.navigate(Routes.MovieList.route) },
                        type = ButtonType.PRIMARY_BUTTON
                    )
                    ButtonComponent(
                        text = "Sign Out",
                        onClick = { navController.navigate(Routes.Login.route) { popUpTo(Routes.MovieList.route) { inclusive = true } } },
                        type = ButtonType.SECONDARY_BUTTON
                    )
                }
            } else {
                uiState.user?.let { user ->
                    if (uiState.isEditMode) {
                        // Edit Mode
                        ProfilePictureComponent(
                            uri = uiState.profilePicPath,
                            isEditing = true,
                            onCameraClick = cameraLauncher
                        )
                        FormFieldComponent(
                            value = uiState.userId,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnUserIdChanged(it)) },
                            label = "User Id",
                            type = FormFieldType.TEXT
                        )
                        FormFieldComponent(
                            value = uiState.preferredName,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnNameChanged(it)) },
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
                            value = uiState.newPassword,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnNewPasswordChanged(it)) },
                            label = "New Password",
                            type = FormFieldType.PASSWORD,
                            isPasswordVisible = uiState.isPasswordVisible,
                            onPasswordVisibilityToggle = { viewModel.onEvent(ProfileUiEvent.TogglePasswordVisibility) }
                        )
                        FormFieldComponent(
                            value = uiState.confirmNewPassword,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnConfirmNewPasswordChanged(it)) },
                            label = "Confirm New Password",
                            type = FormFieldType.PASSWORD,
                            isPasswordVisible = uiState.isConfirmPasswordVisible,
                            onPasswordVisibilityToggle = { viewModel.onEvent(ProfileUiEvent.ToggleConfirmPasswordVisibility) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        ButtonComponent(
                            text = "Save Changes",
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnSaveClicked) },
                            type = ButtonType.PRIMARY_BUTTON
                        )
                        ButtonComponent(
                            text = "Cancel",
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) },
                            type = ButtonType.SECONDARY_BUTTON
                        )
                    } else {
                        // View Mode
                        ProfilePictureComponent(uri = user.profilePicture, isEditing = false)
                        Spacer(modifier = Modifier.height(8.dp))
                        CardComponent {
                            ProfileDetailRow(label = "User ID", value = user.userId)
                            ProfileDetailRow(label = "Preferred Name", value = user.preferredName)
                            ProfileDetailRow(label = "Date of Birth", value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.dateOfBirth))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ButtonComponent(
                            text = "Edit Profile",
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) },
                            type = ButtonType.PRIMARY_BUTTON
                        )
                        ButtonComponent(
                            text = "Sign Out",
                            onClick = { showSignOutDialog = true },
                            type = ButtonType.SECONDARY_BUTTON
                        )
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        ConfirmationDialog(
            onConfirm = {
                showSignOutDialog = false
                // TODO: viewModel.onEvent(ProfileUiEvent.OnLogout)
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.MovieList.route) { inclusive = true }
                }
            },
            onDismiss = { showSignOutDialog = false },
            title = "Sign Out",
            text = "Are you sure you want to sign out?"
        )
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
                        viewModel.onEvent(ProfileUiEvent.OnDobChanged(sdf.format(Date(it))))
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
