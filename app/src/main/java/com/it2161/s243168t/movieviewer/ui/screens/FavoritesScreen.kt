package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.AnimatedListItem
import com.it2161.s243168t.movieviewer.ui.components.ErrorScreen
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieCard
import com.it2161.s243168t.movieviewer.ui.components.NetworkStatusBanner
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEvent

@Composable
fun FavoritesScreen(
    navController: NavController,
    isNetworkConnected: Boolean,
    viewModel: FavoritesViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    currentRoute: String = Routes.Favorites.route
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is FavoritesUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is FavoritesUiEffect.NavigateToDetail -> {
                    navController.navigate(Routes.MovieDetail.createRoute(effect.movieId))
                }
            }
        }
    }

    LaunchedEffect(key1 = true) {
        authViewModel.uiEffect.collect { effect ->
            when (effect) {
                is com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEffect.Navigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(Routes.MovieList.route) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = stringResource(R.string.title_my_favorites),
                canNavigateBack = false,
                onNavigateBack = {},
                showOverflowMenu = true,
                isNetworkConnected = isNetworkConnected,
                onLogout = {
                    authViewModel.onEvent(AuthUiEvent.OnLogoutClicked)
                }
            )
        },
        bottomBar = {
            MovieBottomAppBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    when (route) {
                        Routes.MovieList.route -> navController.navigate(Routes.MovieList.route)
                        Routes.Favorites.route -> {}
                        Routes.Profile.route -> navController.navigate(Routes.Profile.route)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {

            // Content with remaining scaffold padding
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen(loadingType = LoadingType.SKELETON_LIST, itemCount = 3)
                }
                uiState.errorMessage != null -> {
                    ErrorScreen(
                        errorType = ErrorType.GENERIC,
                        message = uiState.errorMessage
                    )
                }
                uiState.movies.isEmpty() -> {
                    ErrorScreen(errorType = ErrorType.EMPTY_FAVORITES)
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimens.PaddingScreenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
                        contentPadding = PaddingValues(vertical = Dimens.PaddingScreenVertical)
                    ) {
                        itemsIndexed(
                            items = uiState.movies,
                            key = { _, movie -> movie.id }
                        ) { index, movie ->
                            AnimatedListItem(index = index, columns = 2) {
                                MovieCard(
                                    movie = movie,
                                    onClick = {
                                        viewModel.onEvent(FavoritesUiEvent.OnMovieClicked(movie.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        }
    }
}
