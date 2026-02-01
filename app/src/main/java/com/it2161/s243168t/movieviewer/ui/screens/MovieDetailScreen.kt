package com.it2161.s243168t.movieviewer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.ui.components.ButtonComponent
import com.it2161.s243168t.movieviewer.ui.components.CardComponent
import com.it2161.s243168t.movieviewer.ui.components.DetailRowComponent
import com.it2161.s243168t.movieviewer.ui.components.GenreChipComponent
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.ReviewItemComponent
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailUiEvent
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailViewModel
import com.it2161.s243168t.movieviewer.utils.formatRevenue
import com.it2161.s243168t.movieviewer.utils.formatRuntime
import java.text.NumberFormat
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.it2161.s243168t.movieviewer.ui.viewmodels.moviedetail.MovieDetailUiEffect
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailScreen(navController: NavController, movieId: Int) {
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

    Scaffold(
        topBar = {
            MovieAppTopAppBar(
                title = "Movie Details",
                canNavigateBack = true,
                onNavigateBack = { navController.popBackStack() }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            // Error state
            if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.errorMessage}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Movie details
            val movie = uiState.movie
            if (movie != null) {
                // Backdrop image with overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                ) {
                    // Backdrop image
                    if (movie.backdropPath.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                "https://image.tmdb.org/t/p/w780${movie.backdropPath}"
                            ),
                            contentDescription = movie.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                    }

                    // Dark overlay gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Title, Rating, and Favorite button overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Movie title
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Rating and Favorite button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Rating badge
                            Row(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⭐ ${String.format(Locale.US, "%.1f", movie.voteAverage)}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }

                            // Favorite button
                            ButtonComponent(
                                text = "",
                                onClick = { viewModel.onEvent(MovieDetailUiEvent.ToggleFavorite(movie.id)) },
                                type = ButtonType.ICON_TEXT,
                                isSelected = uiState.isFavorite,
                                icon = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                iconContentDescription = if (uiState.isFavorite) "Remove from favorites" else "Add to favorites",
                                modifier = Modifier.width(56.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Genres (above the details card)
                if (movie.genres.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        movie.genres.forEach { genre ->
                            GenreChipComponent(genre = genre)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Movie info card with poster and details
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CardComponent {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Poster image
                            if (movie.posterPath.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        "https://image.tmdb.org/t/p/w342${movie.posterPath}"
                                    ),
                                    contentDescription = "${movie.title} poster",
                                    modifier = Modifier
                                        .width(120.dp)
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Details column
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                DetailRowComponent(
                                    icon = Icons.Default.CalendarToday,
                                    label = "Release",
                                    value = movie.releaseDate
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Timer,
                                    label = "Runtime",
                                    value = "${formatRuntime(movie.runtime)}"
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Star,
                                    label = "Rating",
                                    value = "${String.format(Locale.US, "%.1f", movie.voteAverage)}/10"
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.HowToVote,
                                    label = "Votes",
                                    value = NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount)
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Language,
                                    label = "Language",
                                    value = movie.originalLanguage.uppercase()
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.AttachMoney,
                                    label = "Revenue",
                                    value = "${formatRevenue(movie.revenue)}"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Overview card
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    CardComponent {
                        Column {
                            Text(
                                text = "Overview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reviews section
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (uiState.reviews.isEmpty()) {
                        Text(
                            text = "No reviews available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        uiState.reviews.forEach { review ->
                            ReviewItemComponent(review = review)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No movie data available")
                }
            }
        }
    }
}
