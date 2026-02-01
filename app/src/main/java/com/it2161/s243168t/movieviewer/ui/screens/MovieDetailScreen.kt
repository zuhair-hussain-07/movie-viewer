package com.it2161.s243168t.movieviewer.ui.screens

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
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.components.AnimatedFadeIn
import com.it2161.s243168t.movieviewer.ui.components.AnimatedFavoriteButton
import com.it2161.s243168t.movieviewer.ui.components.CardComponent
import com.it2161.s243168t.movieviewer.ui.components.DetailRowComponent
import com.it2161.s243168t.movieviewer.ui.components.ErrorScreen
import com.it2161.s243168t.movieviewer.ui.components.GenreChipComponent
import com.it2161.s243168t.movieviewer.ui.components.LoadingScreen
import com.it2161.s243168t.movieviewer.ui.components.MovieAppTopAppBar
import com.it2161.s243168t.movieviewer.ui.components.RatingBadge
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
                .padding(paddingValues)
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
                // Backdrop image with overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                ) {
                    // Backdrop image with crossfade
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
                            .padding(Dimens.SpacingLg),
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

                        Spacer(modifier = Modifier.width(Dimens.SpacingSm))

                        // Rating and Favorite button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                        ) {
                            // Rating badge
                            RatingBadge(rating = movie.voteAverage)

                            // Favorite button
                            AnimatedFavoriteButton(
                                isFavorite = uiState.isFavorite,
                                onToggle = { viewModel.onEvent(MovieDetailUiEvent.ToggleFavorite(movie.id)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                // Genres (above the details card)
                if (movie.genres.isNotEmpty()) {
                    FlowRow(
                        modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)
                    ) {
                        movie.genres.forEach { genre ->
                            GenreChipComponent(genre = genre)
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                }

                // Movie info card with poster and details
                Box(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    CardComponent {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Poster image with crossfade
                            if (movie.posterPath.isNotEmpty()) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data("https://image.tmdb.org/t/p/w342${movie.posterPath}")
                                        .crossfade(true)
                                        .crossfade(300)
                                        .build(),
                                    contentDescription = stringResource(R.string.cd_movie_poster, movie.title),
                                    modifier = Modifier
                                        .width(Dimens.PosterWidth)
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(Dimens.CornerRadiusMd)),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        ShimmerBox(
                                            modifier = Modifier
                                                .width(Dimens.PosterWidth)
                                                .aspectRatio(2f / 3f)
                                        )
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.width(Dimens.SpacingLg))

                            // Details column
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                DetailRowComponent(
                                    icon = Icons.Default.CalendarToday,
                                    label = stringResource(R.string.label_release),
                                    value = movie.releaseDate
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Timer,
                                    label = stringResource(R.string.label_runtime),
                                    value = formatRuntime(movie.runtime)
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Star,
                                    label = stringResource(R.string.label_rating),
                                    value = stringResource(R.string.format_rating_out_of_ten, String.format(Locale.US, "%.1f", movie.voteAverage))
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.HowToVote,
                                    label = stringResource(R.string.label_votes),
                                    value = NumberFormat.getNumberInstance(Locale.US).format(movie.voteCount)
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.Language,
                                    label = stringResource(R.string.label_language),
                                    value = movie.originalLanguage.uppercase()
                                )
                                DetailRowComponent(
                                    icon = Icons.Default.AttachMoney,
                                    label = stringResource(R.string.label_revenue),
                                    value = formatRevenue(movie.revenue)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                // Overview card
                Box(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    CardComponent {
                        Column {
                            Text(
                                text = stringResource(R.string.label_overview),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = Dimens.SpacingSm)
                            )
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.SpacingLg))

                // Reviews section with animations
                Column(modifier = Modifier.padding(horizontal = Dimens.PaddingScreenHorizontal)) {
                    Text(
                        text = stringResource(R.string.label_reviews),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = Dimens.SpacingSm)
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
