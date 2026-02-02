package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.it2161.s243168t.movieviewer.data.local.enums.LoadingType
import com.it2161.s243168t.movieviewer.ui.theme.Dimens

@Composable
fun LoadingScreen(
    loadingType: LoadingType = LoadingType.SPINNER,
    message: String? = null,
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    when (loadingType) {
        LoadingType.SPINNER -> {
            SpinnerLoading(message = message, modifier = modifier)
        }
        LoadingType.SKELETON_LIST -> {
            SkeletonListLoading(itemCount = itemCount, modifier = modifier)
        }
        LoadingType.SKELETON_DETAIL -> {
            SkeletonDetailLoading(modifier = modifier)
        }
    }
}

@Composable
private fun SpinnerLoading(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.IconSizeXl),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            if (message != null) {
                Spacer(modifier = Modifier.height(Dimens.SpacingLg))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SkeletonListLoading(
    itemCount: Int = 3,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingScreenHorizontal),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLg),
        contentPadding = PaddingValues(vertical = Dimens.PaddingScreenVertical)
    ) {
        items(itemCount) {
            MovieCardSkeleton()
        }
    }
}

@Composable
private fun SkeletonDetailLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.PaddingScreenHorizontal)
    ) {
        // Backdrop skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.MovieDetailBackdropHeight)
                .clip(RoundedCornerShape(Dimens.CornerRadiusLg))
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

        // Title skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(28.dp)
                .clip(RoundedCornerShape(Dimens.CornerRadiusSm))
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingMd))

        // Chips skeleton
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingSm)) {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(Dimens.CornerRadiusXl))
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

        // Info card skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(Dimens.CornerRadiusLg))
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

        // Overview skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(Dimens.CornerRadiusLg))
        )
    }
}

@Composable
fun MovieCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.CornerRadiusLg))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        // Image skeleton
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.MovieCardGridPosterHeight)
                .clip(RoundedCornerShape(topStart = Dimens.CornerRadiusLg, topEnd = Dimens.CornerRadiusLg))
        )

        // Content skeleton
        Column(
            modifier = Modifier.padding(Dimens.PaddingCard)
        ) {
            // Title
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusSm))
            )

            Spacer(modifier = Modifier.height(Dimens.SpacingXs))

            // Category/Genre line
            ShimmerBox(
                modifier = Modifier
                    .width(100.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(Dimens.CornerRadiusSm))
            )
        }
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - 500f, translateAnimation - 500f),
        end = Offset(translateAnimation, translateAnimation)
    )

    Box(
        modifier = modifier.background(brush)
    )
}

@Composable
fun InlineLoading(
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    CircularProgressIndicator(
        modifier = modifier.size(size.dp),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.dp
    )
}
