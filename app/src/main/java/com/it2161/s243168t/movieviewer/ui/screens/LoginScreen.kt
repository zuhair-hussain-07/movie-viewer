package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Theaters
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.FormFieldComponent
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.uiEffect.collect {
            when (it) {
                is AuthUiEffect.Navigate -> navController.navigate(it.route)
                is AuthUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = Dimens.PaddingScreenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Theaters,
                contentDescription = stringResource(R.string.cd_app_logo),
                modifier = Modifier.size(Dimens.IconSizeHuge),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            Text(
                text = stringResource(R.string.title_popcorn_movies),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingXxs))
            Text(
                text = stringResource(R.string.msg_login_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpacingXxxl))

            FormFieldComponent(
                value = uiState.userId,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnUserIdChanged(it)) },
                label = stringResource(R.string.label_user_id),
                type = FormFieldType.TEXT
            )

            FormFieldComponent(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnPasswordChanged(it)) },
                label = stringResource(R.string.label_password),
                type = FormFieldType.PASSWORD,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(AuthUiEvent.TogglePasswordVisibility) }
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingLg))

            if (uiState.isLoading) {
                LoadingScreen(loadingType = LoadingType.SPINNER)
            } else {
                ButtonComponent(
                    text = stringResource(R.string.btn_login),
                    onClick = { viewModel.onEvent(AuthUiEvent.OnLoginClicked) },
                    type = ButtonType.PRIMARY_BUTTON
                )
            }

            TextButton(onClick = { navController.navigate(Routes.Register.route) }) {
                Text(stringResource(R.string.msg_new_here))
            }
        }
    }
}
