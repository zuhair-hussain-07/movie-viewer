package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType

@Composable
fun SearchBarComponent(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    onDisabledClick: () -> Unit = {}
) {
    FormFieldComponent(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        label = "Search movies...",
        type = FormFieldType.TEXT,
        modifier = modifier,
        enabled = isEnabled,
        onDisabledClick = onDisabledClick
    )
}

