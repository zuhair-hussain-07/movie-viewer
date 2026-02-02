package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    columns: Int = 1,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            // Start animation immediately
            targetState = true
        }
    }

    // Calculate row-based delay for grid layouts
    // For a 2-column grid: items 0,1 are row 0 (0ms), items 2,3 are row 1 (50ms), etc.
    val row = index / columns
    val delayMillis = (row.coerceAtMost(5)) * 50

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delayMillis
            ),
            initialOffsetY = { fullHeight -> fullHeight / 3 }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delayMillis
            ),
            initialAlpha = 0f
        ) + scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialScale = 0.92f
        ),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        content()
    }
}

@Composable
fun AnimatedFadeIn(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val delayMillis = (index.coerceAtMost(8)) * 30

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 400,
                delayMillis = delayMillis
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = 350,
                delayMillis = delayMillis
            ),
            initialScale = 0.95f
        ),
        exit = fadeOut(animationSpec = tween(150))
    ) {
        content()
    }
}
