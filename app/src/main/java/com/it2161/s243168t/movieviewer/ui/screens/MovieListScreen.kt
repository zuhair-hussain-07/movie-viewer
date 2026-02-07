package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.AnimatedListItem
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.ErrorScreen
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieCard
import com.it2161.s243168t.movieviewer.ui.components.SearchBarComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEvent

@Composable
fun MovieListScreen(
    navController: NavController,
    isNetworkConnected: Boolean,
    viewModel: MovieViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    currentRoute: String = Routes.MovieList.route
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MovieUiEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is MovieUiEffect.NavigateToDetail -> {
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

    val categories = listOf(
        stringResource(R.string.category_popular),
        stringResource(R.string.category_top_rated),
        stringResource(R.string.category_now_playing),
        stringResource(R.string.category_upcoming)
    )
    val categoryRoutes = listOf("popular", "top_rated", "now_playing", "upcoming")

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = stringResource(R.string.title_discover_movies),
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
                    // Handle bottom navigation
                    when (route) {
                        Routes.MovieList.route -> {}
                        Routes.Favorites.route -> navController.navigate(Routes.Favorites.route)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
            // Search Bar
            SearchBarComponent(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { query ->
                    viewModel.onEvent(MovieUiEvent.OnSearchQueryChanged(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingScreenHorizontal, vertical = Dimens.SpacingXxs)
            )

            // Category Selection (LazyRow)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.PaddingScreenHorizontal, vertical = Dimens.SpacingXxs),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
            ) {
                items(categories.size) { index ->
                    ButtonComponent(
                        text = categories[index],
                        onClick = {
                            viewModel.onEvent(
                                MovieUiEvent.OnCategoryChanged(categoryRoutes[index])
                            )
                        },
                        type = ButtonType.SELECTABLE_BUTTON,
                        isSelected = uiState.selectedCategory == categoryRoutes[index],
                        modifier = Modifier.padding(vertical = Dimens.SpacingXxs)
                    )
                }
            }

            // Movie Feed
            when {
                uiState.isLoading -> {
                    LoadingScreen(
                        loadingType = LoadingType.SKELETON_LIST,
                        itemCount = 3
                    )
                }
                uiState.movies.isEmpty() && !uiState.isOnline -> {
                    ErrorScreen(
                        errorType = ErrorType.NO_INTERNET,
                        onRetry = { viewModel.onEvent(MovieUiEvent.RefreshList) }
                    )
                }
                uiState.movies.isEmpty() -> {
                    ErrorScreen(
                        errorType = ErrorType.NO_DATA,
                        onRetry = { viewModel.onEvent(MovieUiEvent.RefreshList) }
                    )
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
                                        viewModel.onEvent(MovieUiEvent.OnMovieClicked(movie.id))
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
