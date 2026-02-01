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
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            // Start animation immediately
            targetState = true
        }
    }

    // Calculate staggered delay based on index (max 5 items staggered)
    val delayMillis = (index.coerceAtMost(5)) * 50

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
fun AnimatedListItemHorizontal(
    index: Int,
    fromRight: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    val delayMillis = (index.coerceAtMost(5)) * 50
    val direction = if (fromRight) 1 else -1

    AnimatedVisibility(
        visibleState = visibleState,
        modifier = modifier,
        enter = slideInVertically(
            animationSpec = tween(
                durationMillis = 350,
                delayMillis = delayMillis
            ),
            initialOffsetY = { fullHeight -> (fullHeight / 4) * direction }
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = delayMillis
            )
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
