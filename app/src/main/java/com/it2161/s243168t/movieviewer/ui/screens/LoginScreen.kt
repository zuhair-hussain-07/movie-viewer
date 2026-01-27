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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.FormFieldComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
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
                is AuthUiEffect.ShowToast -> snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Theaters,
                contentDescription = "PopCornMovie Logo",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Welcome Back", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Login to continue your cinematic journey",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            FormFieldComponent(
                value = uiState.userId,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnUserIdChanged(it)) },
                label = "User ID",
                type = FormFieldType.TEXT
            )

            FormFieldComponent(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(AuthUiEvent.OnPasswordChanged(it)) },
                label = "Password",
                type = FormFieldType.PASSWORD,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordVisibilityToggle = { viewModel.onEvent(AuthUiEvent.TogglePasswordVisibility) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                ButtonComponent(
                    text = "Login",
                    onClick = { viewModel.onEvent(AuthUiEvent.OnLoginClicked) },
                    type = ButtonType.PRIMARY_BUTTON
                )
            }

            TextButton(onClick = { navController.navigate(Routes.Register.route) }) {
                Text("New here? Create Account")
            }
        }
    }
}
