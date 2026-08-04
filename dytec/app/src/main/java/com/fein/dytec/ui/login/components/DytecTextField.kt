package com.fein.dytec.ui.login.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.fein.dytec.ui.theme.DytecTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DytecTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { 
            Text(
                text = label, 
                fontWeight = FontWeight.Bold
            ) 
        },
        leadingIcon = { Icon(imageVector = icon, contentDescription = null) },
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = DytecTheme.colors.fieldBorder,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DytecTheme.colors.fieldBg,
            unfocusedContainerColor = DytecTheme.colors.fieldBg,
            errorContainerColor = DytecTheme.colors.fieldBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = DytecTheme.colors.textDark,
            unfocusedTextColor = DytecTheme.colors.textDark,
            focusedLabelColor = DytecTheme.colors.textLight,
            unfocusedLabelColor = DytecTheme.colors.textLight,
            focusedLeadingIconColor = DytecTheme.colors.textLight,
            unfocusedLeadingIconColor = DytecTheme.colors.textLight
        ),
        shape = RoundedCornerShape(16.dp)
    )
}
