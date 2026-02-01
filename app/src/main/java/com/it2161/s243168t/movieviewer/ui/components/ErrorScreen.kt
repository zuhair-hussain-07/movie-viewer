package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.it2161.s243168t.movieviewer.R
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType
import com.it2161.s243168t.movieviewer.data.local.enums.ErrorType
import com.it2161.s243168t.movieviewer.ui.theme.Dimens

@Composable
fun ErrorScreen(
    errorType: ErrorType = ErrorType.GENERIC,
    title: String? = null,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (defaultIcon, defaultTitle, defaultMessage) = getErrorContent(errorType)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.SpacingXxxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = defaultIcon,
            contentDescription = stringResource(R.string.cd_error_icon),
            modifier = Modifier.size(Dimens.IconSizeXxl),
            tint = when (errorType) {
                ErrorType.GENERIC, ErrorType.NO_INTERNET -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingLg))

        Text(
            text = title ?: defaultTitle,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.SpacingSm))

        Text(
            text = message ?: defaultMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (onRetry != null) {
            Spacer(modifier = Modifier.height(Dimens.SpacingXxl))

            ButtonComponent(
                text = stringResource(R.string.btn_retry),
                onClick = onRetry,
                type = ButtonType.PRIMARY_BUTTON
            )
        }
    }
}

@Composable
private fun getErrorContent(errorType: ErrorType): Triple<ImageVector, String, String> {
    return when (errorType) {
        ErrorType.GENERIC -> Triple(
            Icons.Default.Error,
            stringResource(R.string.error_generic),
            stringResource(R.string.error_loading_failed)
        )
        ErrorType.NO_INTERNET -> Triple(
            Icons.Default.WifiOff,
            stringResource(R.string.msg_no_cached_data),
            stringResource(R.string.msg_offline_description)
        )
        ErrorType.NO_DATA -> Triple(
            Icons.Default.CloudOff,
            stringResource(R.string.msg_no_movies_found),
            stringResource(R.string.error_loading_failed)
        )
        ErrorType.EMPTY_FAVORITES -> Triple(
            Icons.Default.SentimentDissatisfied,
            stringResource(R.string.msg_no_favorites_yet),
            stringResource(R.string.msg_add_favorites_hint)
        )
        ErrorType.EMPTY_SEARCH -> Triple(
            Icons.Default.SearchOff,
            stringResource(R.string.msg_no_movies_found),
            stringResource(R.string.error_loading_failed)
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(Dimens.SpacingLg)
    )
}
