package com.it2161.s243168t.movieviewer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object AppShapeTokens {
    val CornerExtraSmall = RoundedCornerShape(4.dp)
    val CornerSmall = RoundedCornerShape(8.dp)
    val CornerMedium = RoundedCornerShape(12.dp)
    val CornerLarge = RoundedCornerShape(16.dp)
    val CornerExtraLarge = RoundedCornerShape(24.dp)
    val CornerFull = RoundedCornerShape(50)

    // Specific component shapes
    val Card = RoundedCornerShape(12.dp)
    val CardTopOnly = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
    val Button = RoundedCornerShape(8.dp)
    val Chip = RoundedCornerShape(16.dp)
    val TextField = RoundedCornerShape(8.dp)
    val Badge = RoundedCornerShape(8.dp)
    val BottomSheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
