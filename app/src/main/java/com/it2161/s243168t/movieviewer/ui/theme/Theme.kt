package com.it2161.s243168t.movieviewer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// Dark theme color scheme with dark blue primary
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = DarkBlue60,
    onPrimary = Neutral10,
    primaryContainer = DarkBlue30,
    onPrimaryContainer = DarkBlue90,
    inversePrimary = DarkBlue40,

    // Secondary colors
    secondary = Teal60,
    onSecondary = Teal10,
    secondaryContainer = Teal30,
    onSecondaryContainer = Teal90,

    // Tertiary colors
    tertiary = Amber60,
    onTertiary = Amber10,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,

    // Background colors
    background = Neutral10,
    onBackground = Neutral90,

    // Surface colors
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceTint = DarkBlue60,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,

    // Error colors
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,

    // Outline colors
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,

    // Scrim
    scrim = Neutral0
)

// Light theme color scheme with dark blue primary
private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = DarkBlue40,
    onPrimary = Neutral100,
    primaryContainer = DarkBlue90,
    onPrimaryContainer = DarkBlue10,
    inversePrimary = DarkBlue80,

    // Secondary colors
    secondary = Teal40,
    onSecondary = Neutral100,
    secondaryContainer = Teal90,
    onSecondaryContainer = Teal10,

    // Tertiary colors
    tertiary = Amber40,
    onTertiary = Neutral100,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,

    // Background colors
    background = Neutral99,
    onBackground = Neutral10,

    // Surface colors
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceTint = DarkBlue40,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,

    // Error colors
    error = Error40,
    onError = Neutral100,
    errorContainer = Error90,
    onErrorContainer = Error10,

    // Outline colors
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,

    // Scrim
    scrim = Neutral0
)

// Custom shapes following 4dp grid system
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun _243168TMovieViewerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic color to use our custom dark blue color scheme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Update status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}