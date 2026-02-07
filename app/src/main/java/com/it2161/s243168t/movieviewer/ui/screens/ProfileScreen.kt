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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.CardComponent
import com.it2161.s243168t.movieviewer.ui.components.ConfirmationDialog
import com.it2161.s243168t.movieviewer.ui.components.DetailRowComponent
import com.it2161.s243168t.movieviewer.ui.components.ErrorScreen
import com.it2161.s243168t.movieviewer.ui.components.FormFieldComponent
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.ProfilePictureComponent
import com.it2161.s243168t.movieviewer.ui.components.ProfilePictureComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
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
    isNetworkConnected: Boolean,
    viewModel: ProfileViewModel = hiltViewModel(),
    currentRoute: String = Routes.Profile.route
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
                is ProfileUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(it.message)
                ProfileUiEffect.NavigateBack -> navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = if (uiState.isEditMode) stringResource(R.string.title_edit_profile) else stringResource(R.string.title_my_profile),
                canNavigateBack = uiState.isEditMode,
                onNavigateBack = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) },
                isNetworkConnected = isNetworkConnected
            ) {
                if (!uiState.isEditMode) {
                    IconButton(onClick = { showSignOutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = stringResource(R.string.btn_sign_out)
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
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {

            // Content with remaining scaffold padding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
                    .padding(horizontal = Dimens.PaddingScreenHorizontal)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
            if (uiState.isLoading) {
                LoadingScreen(loadingType = LoadingType.SPINNER)
            } else if (uiState.user == null) {
                // Handle null user case - show error and redirect
                ErrorScreen(
                    errorType = ErrorType.GENERIC,
                    title = stringResource(R.string.msg_unable_to_load_profile),
                    message = stringResource(R.string.msg_login_again),
                    onRetry = null
                )
                Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                ButtonComponent(
                    text = stringResource(R.string.btn_back_to_home),
                    onClick = { navController.navigate(Routes.MovieList.route) },
                    type = ButtonType.PRIMARY_BUTTON
                )
                ButtonComponent(
                    text = stringResource(R.string.btn_sign_out),
                    onClick = { navController.navigate(Routes.Login.route) { popUpTo(Routes.MovieList.route) { inclusive = true } } },
                    type = ButtonType.SECONDARY_BUTTON
                )
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
                            label = stringResource(R.string.label_user_id),
                            type = FormFieldType.TEXT
                        )
                        FormFieldComponent(
                            value = uiState.preferredName,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnNameChanged(it)) },
                            label = stringResource(R.string.label_preferred_name),
                            type = FormFieldType.TEXT
                        )
                        FormFieldComponent(
                            value = uiState.dob,
                            onValueChange = {},
                            label = stringResource(R.string.label_date_of_birth),
                            type = FormFieldType.DATE,
                            onTrailingIconClick = { showDatePicker = true }
                        )
                        FormFieldComponent(
                            value = uiState.newPassword,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnNewPasswordChanged(it)) },
                            label = stringResource(R.string.label_new_password),
                            type = FormFieldType.PASSWORD,
                            isPasswordVisible = uiState.isPasswordVisible,
                            onPasswordVisibilityToggle = { viewModel.onEvent(ProfileUiEvent.TogglePasswordVisibility) }
                        )
                        FormFieldComponent(
                            value = uiState.confirmNewPassword,
                            onValueChange = { viewModel.onEvent(ProfileUiEvent.OnConfirmNewPasswordChanged(it)) },
                            label = stringResource(R.string.label_confirm_new_password),
                            type = FormFieldType.PASSWORD,
                            isPasswordVisible = uiState.isConfirmPasswordVisible,
                            onPasswordVisibilityToggle = { viewModel.onEvent(ProfileUiEvent.ToggleConfirmPasswordVisibility) }
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                        ButtonComponent(
                            text = stringResource(R.string.btn_save_changes),
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnSaveClicked) },
                            type = ButtonType.PRIMARY_BUTTON
                        )
                        ButtonComponent(
                            text = stringResource(R.string.btn_cancel),
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) },
                            type = ButtonType.SECONDARY_BUTTON
                        )
                    } else {
                        // View Mode
                        ProfilePictureComponent(uri = user.profilePicture, isEditing = false)
                        Spacer(modifier = Modifier.height(Dimens.SpacingSm))
                        CardComponent {
                            DetailRowComponent(
                                icon = Icons.Filled.AccountCircle,
                                label = stringResource(R.string.label_user_id),
                                value = user.userId
                            )
                            DetailRowComponent(
                                icon = Icons.Filled.Person,
                                label = stringResource(R.string.label_preferred_name),
                                value = user.preferredName
                            )
                            DetailRowComponent(
                                icon = Icons.Filled.CalendarToday,
                                label = stringResource(R.string.label_date_of_birth),
                                value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.dateOfBirth)
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                        ButtonComponent(
                            text = stringResource(R.string.btn_edit_profile),
                            onClick = { viewModel.onEvent(ProfileUiEvent.OnEditToggle) },
                            type = ButtonType.PRIMARY_BUTTON
                        )
                        ButtonComponent(
                            text = stringResource(R.string.btn_sign_out),
                            onClick = { showSignOutDialog = true },
                            type = ButtonType.SECONDARY_BUTTON
                        )
                    }
                }
            }
            }
        }
    }

    if (showSignOutDialog) {
        ConfirmationDialog(
            onConfirm = {
                showSignOutDialog = false
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.MovieList.route) { inclusive = true }
                }
            },
            onDismiss = { showSignOutDialog = false },
            title = stringResource(R.string.title_sign_out),
            text = stringResource(R.string.msg_sign_out_confirmation)
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
                    Text(stringResource(R.string.btn_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
