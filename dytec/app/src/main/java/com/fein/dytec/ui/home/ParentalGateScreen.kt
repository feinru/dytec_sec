package com.fein.dytec.ui.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.login.components.DytecBackButton
import com.fein.dytec.ui.theme.*

@Composable
fun ParentalGateScreen(
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val a = remember { (2..5).random() }
    val x = remember { (2..9).random() }
    val b = remember { (1..10).random() }
    val c = a * x + b
    
    var answer by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

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
                text = "Area Orang Tua",
                fontSize = 32.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Berapakah nilai x jika \n$a" + "x + $b = $c ?",
                fontSize = 18.sp,
                color = DytecTheme.colors.textLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
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
                    value = answer,
                    onValueChange = { newValue ->
                        if (newValue.length <= 2) {
                            answer = newValue
                            hasError = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                        autoCorrect = false
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (answer.trim() == x.toString()) {
                                onSuccess()
                            } else {
                                hasError = true
                            }
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
                        if (answer.isEmpty()) Color.Transparent else PrimaryGreen
                    ),
                    decorationBox = { innerTextField ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                if (answer.isEmpty() && !isFocused) {
                                    Text(
                                        text = "Jawaban",
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DytecTheme.colors.fieldBorder,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (answer.isEmpty() && isFocused) {
                                    val infiniteTransition = rememberInfiniteTransition(label = "")
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
            
            if (hasError) {
                Text(
                    text = "Jawaban salah, coba lagi.",
                    color = PrimaryRed,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
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
                text = "MASUK",
                onClick = {
                    if (answer.trim() == x.toString()) {
                        onSuccess()
                    } else {
                        hasError = true
                    }
                },
                color = PrimaryGreen,
                shadowColor = PrimaryGreenShadow
            )
        }

        // Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DytecBackButton(onClick = onNavigateBack)
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}
