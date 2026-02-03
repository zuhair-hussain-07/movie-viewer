package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.AnimatedFadeIn
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.ErrorScreen
import com.it2161.s243168t.movieviewer.ui.components.GenreChipComponent
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.NetworkStatusBanner
import com.it2161.s243168t.movieviewer.ui.components.ReviewItemComponent
import com.it2161.s243168t.movieviewer.ui.components.ShimmerBox
import com.it2161.s243168t.movieviewer.ui.navigation.Routes
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailUiEffect
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthViewModel
import com.it2161.s243168t.movieviewer.ui.viewmodels.authentication.AuthUiEvent
import com.it2161.s243168t.movieviewer.utils.formatRevenue
import com.it2161.s243168t.movieviewer.utils.formatRuntime
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(
    navController: NavController,
    movieId: Int,
    isNetworkConnected: Boolean,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val viewModel: MovieDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MovieDetailUiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                MovieDetailUiEffect.NavigateBack -> navController.popBackStack()
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
                title = stringResource(R.string.title_movie_details),
                canNavigateBack = true,
                onNavigateBack = { navController.popBackStack() },
                showOverflowMenu = true,
                onLogout = {
                    authViewModel.onEvent(AuthUiEvent.OnLogoutClicked)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            // Network Status Banner (sits directly below TopAppBar)
            NetworkStatusBanner(isConnected = isNetworkConnected)

            // Content with remaining scaffold padding
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
            // Loading state
            if (uiState.isLoading) {
                LoadingScreen(loadingType = LoadingType.SKELETON_DETAIL)
                return@Scaffold
            }

            // Error state
            if (uiState.errorMessage != null) {
                ErrorScreen(
                    errorType = ErrorType.GENERIC,
                    message = uiState.errorMessage
                )
                return@Scaffold
            }

            // Movie details
            val movie = uiState.movie
            if (movie != null) {
                // 1. HERO HEADER - Cinematic backdrop with gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    // Backdrop image
                    if (movie.backdropPath.isNotEmpty()) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://image.tmdb.org/t/p/w780${movie.backdropPath}")
                                .crossfade(true)
                                .crossfade(300)
                                .build(),
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                ShimmerBox(modifier = Modifier.fillMaxSize())
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    // Gradient overlay (transparent to black)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.8f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )

                    // Title and Metadata at bottom
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(Dimens.SpacingLg)
                    ) {
                        // Movie Title
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(Dimens.SpacingXs))

                        // 2. METADATA ROW - Year • Runtime • Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingXs)
                        ) {
                            // Release Year
                            Text(
                                text = movie.releaseDate.take(4),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )

                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            // Runtime
                            Text(
                                text = formatRuntime(movie.runtime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )

                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.6f)
                            )

                            // Star Rating
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700), // Gold color
                                modifier = Modifier.padding(end = 2.dp).size(16.dp)
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", movie.voteAverage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                // 3. ACTION ROW - Favorite Button + Genre Chips
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)
                ) {
                    // Favorite Button
                    ButtonComponent(
                        text = if (uiState.isFavorite) {
                            "Remove from Favorites"
                        } else {
                            "Add to Favorites"
                        },
                        onClick = { viewModel.onEvent(MovieDetailUiEvent.ToggleFavorite(movie.id)) },
                        type = ButtonType.FAVORITE_BUTTON,
                        isSelected = uiState.isFavorite,
                        icon = if (uiState.isFavorite) {
                            Icons.Default.Favorite
                        } else {
                            Icons.Default.FavoriteBorder
                        },
                        modifier = Modifier
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpacingMd))

                    // Genre Chips in FlowRow
                    if (movie.genres.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                            verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                        ) {
                            movie.genres.forEach { genre ->
                                GenreChipComponent(genre = genre)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                // 4. CONTENT BODY - Storyline (Overview)
                Column(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    Text(
                        text = "Storyline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.SpacingMd)
                    )
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                // Details Grid (2 columns) - Muted, less prominent
                Column(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.SpacingMd)
                    )

                    // Grid of details
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMd)
                    ) {
                        // Row 1: Adult + Genres
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Adult",
                                    value = if (movie.adult) "Yes" else "No"
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Original Language",
                                    value = movie.originalLanguage.uppercase()
                                )
                            }
                        }

                        // Row 2: Release Date + Runtime
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Release Date",
                                    value = movie.releaseDate
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Runtime",
                                    value = formatRuntime(movie.runtime)
                                )
                            }
                        }

                        // Row 3: Vote Count + Vote Average
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Vote Count",
                                    value = NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Vote Average",
                                    value = String.format(Locale.US, "%.1f / 10", movie.voteAverage)
                                )
                            }
                        }

                        // Row 4: Revenue
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                DetailItem(
                                    label = "Revenue",
                                    value = formatRevenue(movie.revenue)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                // Empty column to maintain grid structure
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingXl))

                // Reviews section
                Column(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    Text(
                        text = stringResource(R.string.label_reviews),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.SpacingMd)
                    )

                    if (uiState.reviews.isEmpty()) {
                        Text(
                            text = stringResource(R.string.msg_no_reviews_available),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.reviews.forEachIndexed { index, review ->
                            AnimatedFadeIn(index = index) {
                                ReviewItemComponent(review = review)
                            }
                            Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))
            } else {
                ErrorScreen(
                    errorType = ErrorType.NO_DATA,
                    title = stringResource(R.string.msg_no_movie_data)
                )
            }
            }
        }
    }
}

// Helper composable for detail grid items
@Composable
private fun DetailItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

