package com.fein.dytec.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.theme.*

import com.fein.dytec.presentation.onboarding.OnboardingState
import com.fein.dytec.presentation.onboarding.OnboardingEvent

@Composable
fun NameScreen(
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
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
                text = "Siapa namamu?",
                fontSize = 32.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Buat nama panggilan yang paling keren!",
                fontSize = 16.sp,
                color = DytecTheme.colors.textLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + androidx.compose.animation.fadeIn(animationSpec = tween(500))
            ) {
                // Playful 3D name input
                BasicTextField(
                    value = state.name,
                    onValueChange = { newValue ->
                        if (newValue.length <= 12) {
                            onEvent(OnboardingEvent.NameChanged(newValue))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                        imeAction = ImeAction.Done,
                        autoCorrect = false
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (state.isNameValid) onContinue()
                        }
                    ),
                    interactionSource = interactionSource,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryGreen,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(
                        if (state.name.isEmpty()) Color.Transparent else PrimaryGreen
                    ),
                    decorationBox = { innerTextField ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.name.isEmpty() && !isFocused) {
                                    Text(
                                        text = "Nama",
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DytecTheme.colors.fieldBorder,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (state.name.isEmpty() && isFocused) {
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
                                            .height(50.dp)
                                            .background(PrimaryGreen.copy(alpha = alpha))
                                    )
                                }
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    innerTextField()
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(6.dp)
                                    .background(
                                        DytecTheme.colors.fieldBorder, 
                                        RoundedCornerShape(3.dp)
                                    )
                            )
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1.5f))
        }
        
        // Bottom Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
        ) {
            
            DytecButton(
                text = "LANJUTKAN",
                onClick = onContinue,
                enabled = state.isNameValid
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
