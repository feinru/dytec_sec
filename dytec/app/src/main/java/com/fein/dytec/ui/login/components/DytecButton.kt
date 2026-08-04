package com.fein.dytec.ui.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.theme.PrimaryGreen
import com.fein.dytec.ui.theme.PrimaryGreenShadow
import com.fein.dytec.ui.theme.DytecTheme

@Composable
fun DytecButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isSecondary: Boolean = false,
    color: Color? = null,
    shadowColor: Color? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3D Push effect
    val yOffset = if (isPressed && enabled) 4.dp else 0.dp
    
    val mainColor = color ?: if (isSecondary) DytecTheme.colors.bgWhite else PrimaryGreen
    val finalShadowColor = shadowColor ?: if (isSecondary) DytecTheme.colors.fieldBorder else PrimaryGreenShadow
    val textColor = if (color != null) Color.White else if (isSecondary) PrimaryGreen else Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) finalShadowColor else DytecTheme.colors.fieldBorder)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .offset(y = yOffset)
                .clip(RoundedCornerShape(16.dp))
                .background(if (enabled) mainColor else DytecTheme.colors.fieldBorder.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = textColor,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = text.uppercase(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun DytecBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val yOffset = if (isPressed) 4.dp else 0.dp

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DytecTheme.colors.fieldBorder)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-4).dp + yOffset)
                .clip(RoundedCornerShape(12.dp))
                .background(DytecTheme.colors.fieldBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Kembali",
                tint = DytecTheme.colors.textDark,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
