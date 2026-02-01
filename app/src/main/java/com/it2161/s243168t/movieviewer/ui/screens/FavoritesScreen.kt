package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieCard
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.favorites.FavoritesViewModel

@Composable
fun FavoritesScreen(
    navController: NavController,
    currentRoute: String = Routes.Favorites.route,
    viewModel: FavoritesViewModel = hiltViewModel()
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

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = "My Favorites",
                canNavigateBack = false,
                onNavigateBack = {}
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentDissatisfied,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.errorMessage ?: "An error occurred",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                uiState.movies.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentDissatisfied,
                            contentDescription = "No favorites",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "No favorites yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Start adding movies to your favorites by tapping the heart icon on any movie card.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(
                            items = uiState.movies,
                            key = { movie -> movie.id }
                        ) { movie ->
                            MovieCard(
                                movie = movie,
                                onClick = {
                                    viewModel.onEvent(FavoritesUiEvent.OnMovieClicked(movie.id))
                                },
                                isFavorite = movie.id in uiState.favoriteIds,
                                onToggleFavorite = {
                                    viewModel.onEvent(FavoritesUiEvent.ToggleFavorite(movie))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
