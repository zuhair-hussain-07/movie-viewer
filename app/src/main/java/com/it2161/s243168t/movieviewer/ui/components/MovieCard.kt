package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import com.it2161.s243168t.movieviewer.ui.theme.Dimens

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Card press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val cardElevation by animateFloatAsState(
        targetValue = if (isPressed) 2f else 4f,
        animationSpec = tween(durationMillis = 150),
        label = "card_elevation"
    )

    Card(
        modifier = modifier
            .scale(cardScale)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true),
                onClick = onClick
            ),
        shape = RoundedCornerShape(Dimens.CornerRadiusLg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = cardElevation.dp
        )
    ) {
        Column {
            // Movie Poster Image with crossfade
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.MovieCardGridPosterHeight)
                    .clip(RoundedCornerShape(topStart = Dimens.CornerRadiusLg, topEnd = Dimens.CornerRadiusLg))
            ) {
                if (movie.posterPath.isNotEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                            .crossfade(true)
                            .crossfade(300)
                            .build(),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.MovieCardGridPosterHeight),
                        contentScale = ContentScale.Crop,
                        loading = {
                            ShimmerBox(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens.MovieCardGridPosterHeight)
                            )
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimens.MovieCardGridPosterHeight)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    )
                } else {
                    // Placeholder if no poster image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.MovieCardGridPosterHeight)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }

                // Rating Badge (Top Right only)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd)
                        .padding(Dimens.SpacingMd),
                    contentAlignment = Alignment.TopEnd
                ) {
                    RatingBadge(rating = movie.voteAverage)
                }
            }

            // Movie Info - Title only
            Column(
                modifier = Modifier.padding(Dimens.PaddingCard)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


