package com.it2161.s243168t.movieviewer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.it2161.s243168t.movieviewer.data.local.enums.FormFieldType

@Composable
fun FormFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    type: FormFieldType,
    modifier: Modifier = Modifier,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityToggle: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {},
    errorMessage: String? = null
) {
    val isError = errorMessage != null
    val isClickableField = type == FormFieldType.DATE || type == FormFieldType.PHOTO

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (type == FormFieldType.PASSWORD && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            readOnly = isClickableField,
            isError = isError,
            supportingText = if (isError) {
                { Text(errorMessage!!) }
            } else null,
            trailingIcon = {
                when (type) {
                    FormFieldType.PASSWORD -> {
                        IconButton(onClick = onPasswordVisibilityToggle) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    }
                    FormFieldType.DATE -> {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "Select date")
                        }
                    }
                    FormFieldType.PHOTO -> {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                        }
                    }
                    else -> null
                }
            }
        )

        if (isClickableField) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onTrailingIconClick() }
            )
        }
    }
}