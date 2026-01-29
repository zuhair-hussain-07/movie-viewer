package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieBottomAppBar
import com.it2161.s243168t.movieviewer.ui.components.MovieCard
import com.it2161.s243168t.movieviewer.ui.components.SearchBarComponent
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.movielist.MovieViewModel

@Composable
fun MovieListScreen(
    navController: NavController,
    currentRoute: String = "home",
    viewModel: MovieViewModel = hiltViewModel()
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
                    // TODO: Navigate to movie detail screen
                    snackbarHostState.showSnackbar("Movie ${effect.movieId} clicked")
                }
            }
        }
    }

    val categories = listOf("Popular", "Top Rated", "Now Playing")
    val categoryRoutes = listOf("popular", "top_rated", "now_playing")

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = "Discover Movies",
                canNavigateBack = false,
                onNavigateBack = {}
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options"
                    )
                }
            }
        },
        bottomBar = {
            MovieBottomAppBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    // Handle bottom navigation
                    when (route) {
                        "home" -> {}
                        "favorites" -> navController.navigate(Routes.Favorites.route)
                        "profile" -> navController.navigate(Routes.Profile.route)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBarComponent(
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = { query ->
                    viewModel.onEvent(MovieUiEvent.OnSearchQueryChanged(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Category Selection (LazyRow)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                        modifier = Modifier.padding(vertical = 0.dp)
                    )
                }
            }

            // Movie Feed
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.movies.isEmpty() && !uiState.isOnline -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No cached data available",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "You appear to be offline. Please connect to the internet to load movies.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            ButtonComponent(
                                text = "Retry",
                                onClick = {
                                    viewModel.onEvent(MovieUiEvent.RefreshList)
                                },
                                type = ButtonType.PRIMARY_BUTTON,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
                uiState.movies.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No movies found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            ButtonComponent(
                                text = "Retry",
                                onClick = {
                                    viewModel.onEvent(MovieUiEvent.RefreshList)
                                },
                                type = ButtonType.PRIMARY_BUTTON,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(
                            items = uiState.movies,
                            key = { movie -> movie.id }
                        ) { movie ->
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

