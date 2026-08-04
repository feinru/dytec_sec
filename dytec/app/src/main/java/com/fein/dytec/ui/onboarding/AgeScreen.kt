package com.fein.dytec.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.login.components.DytecTextField
import com.fein.dytec.ui.theme.DytecTheme
import com.fein.dytec.ui.theme.PrimaryGreen

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle

import com.fein.dytec.presentation.onboarding.OnboardingState
import com.fein.dytec.presentation.onboarding.OnboardingEvent

@Composable
fun AgeScreen(
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            
            Text(
                text = "Berapa umurmu?",
                fontSize = 32.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Sesuaikan umurmu agar tesnya cocok buat kamu.",
                fontSize = 16.sp,
                color = DytecTheme.colors.textLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
            
            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + androidx.compose.animation.fadeIn(animationSpec = tween(500))
            ) {
                // Playful 3D age input
                BasicTextField(
                    value = state.age,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                            onEvent(OnboardingEvent.AgeChanged(newValue))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.isAgeValid) {
                                onContinue()
                            }
                        }
                    ),
                    interactionSource = interactionSource,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 100.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreen,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(
                        if (state.age.isEmpty()) androidx.compose.ui.graphics.Color.Transparent else PrimaryGreen
                    ),
                    decorationBox = { innerTextField ->
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.age.isEmpty() && !isFocused) {
                                    Text(
                                        text = "0",
                                        fontSize = 100.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = com.fein.dytec.ui.theme.DytecTheme.colors.fieldBorder,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (state.age.isEmpty() && isFocused) {
                                    val infiniteTransition = rememberInfiniteTransition()
                                    val alpha by infiniteTransition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 0f,
                                        animationSpec = infiniteRepeatable(
                                            animation = keyframes {
                                                durationMillis = 1000
                                                1f at 0
                                                1f at 499
                                                0f at 500
                                                0f at 1000
                                            },
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "cursorBlink"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(90.dp)
                                            .background(PrimaryGreen.copy(alpha = alpha))
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    innerTextField()
                                }
                            }
                            
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(6.dp)
                                    .background(
                                        com.fein.dytec.ui.theme.DytecTheme.colors.fieldBorder, 
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                )
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1.5f))
        }
        
        // Bottom Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            DytecButton(
                text = "LANJUTKAN",
                onClick = onContinue,
                enabled = state.isAgeValid
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DytecButton(
                text = "KEMBALI",
                onClick = onBack,
                isSecondary = true
            )
        }
    }
}
