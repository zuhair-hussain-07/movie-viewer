package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.it2161.s243168t.movieviewer.data.local.enums.ButtonType

@Composable
fun ButtonComponent(
    text: String,
    onClick: () -> Unit,
    type: ButtonType,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val buttonModifier = modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)

    val shape = RoundedCornerShape(8.dp)

    when (type) {
        ButtonType.PRIMARY_BUTTON -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape
            ) {
                Text(text)
            }
        }
        ButtonType.SECONDARY_BUTTON -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape
            ) {
                Text(text)
            }
        }
        ButtonType.TEXT -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = shape
            ) {
                Text(text)
            }
        }
    }
}