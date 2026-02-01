package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.ui.theme.Dimens
import java.util.Locale

@Composable
fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                shape = RoundedCornerShape(Dimens.CornerRadiusMd)
            )
            .padding(horizontal = Dimens.SpacingSm, vertical = Dimens.SpacingXs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.format_rating, String.format(Locale.US, "%.1f", rating)),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
