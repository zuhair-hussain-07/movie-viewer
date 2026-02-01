package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.models.Movie
import java.util.Locale

@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column {
            // Movie Backdrop Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                if (movie.backdropPath.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            "https://image.tmdb.org/t/p/w500${movie.backdropPath}"
                        ),
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder if no backdrop image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }

                // Top Row: Favorite button (left) and Rating Badge (right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Favorite Button (Top Left)
                    if (onToggleFavorite != null) {
                        ButtonComponent(
                            text = "",
                            onClick = onToggleFavorite,
                            type = ButtonType.ICON_TEXT,
                            isSelected = isFavorite,
                            icon = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            iconContentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            modifier = Modifier
                        )
                    }

                    // Rating Badge (Top Right)
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⭐ ${String.format(Locale.US, "%.1f", movie.voteAverage)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            // Movie Info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = movie.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = movie.overview,
                    fontSize = 13.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Release: ${movie.releaseDate}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

