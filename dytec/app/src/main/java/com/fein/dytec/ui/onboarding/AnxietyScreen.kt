package com.fein.dytec.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.theme.*

import com.fein.dytec.presentation.onboarding.OnboardingState
import com.fein.dytec.presentation.onboarding.OnboardingEvent

@Composable
fun AnxietyScreen(
    state: OnboardingState,
    onEvent: (OnboardingEvent) -> Unit,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
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
                .padding(top = 80.dp, bottom = 120.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            
            Text(
                text = "Jujur aja nih...",
                fontSize = 32.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Bagaimana perasaanmu saat belajar matematika?",
                fontSize = 16.sp,
                color = DytecTheme.colors.textLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AnxietyOptionCard(
                        emoji = "😍",
                        label = "Suka Banget!",
                        color = PrimaryGreen,
                        shadowColor = PrimaryGreenShadow,
                        isSelected = state.selectedEmotion == "Suka",
                        onClick = { onEvent(OnboardingEvent.EmotionSelected("Suka")) }
                    )
                    
                    AnxietyOptionCard(
                        emoji = "😐",
                        label = "Biasa Saja",
                        color = PrimaryOrange,
                        shadowColor = PrimaryOrangeShadow,
                        isSelected = state.selectedEmotion == "Biasa",
                        onClick = { onEvent(OnboardingEvent.EmotionSelected("Biasa")) }
                    )
                    
                    AnxietyOptionCard(
                        emoji = "😰",
                        label = "Takut / Bingung",
                        color = PrimaryRed,
                        shadowColor = PrimaryRedShadow,
                        isSelected = state.selectedEmotion == "Takut",
                        onClick = { onEvent(OnboardingEvent.EmotionSelected("Takut")) }
                    )
                }
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
                enabled = state.selectedEmotion != null
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

@Composable
fun AnxietyOptionCard(
    emoji: String,
    label: String,
    color: Color,
    shadowColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    
    val bgMain = if (isSelected) color else DytecTheme.colors.fieldBg
    val bgShadow = if (isSelected) shadowColor else DytecTheme.colors.fieldBorder
    val textColor = if (isSelected) Color.White else DytecTheme.colors.textDark
    
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .background(bgShadow)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isSelected) 74.dp else 76.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(bgMain)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = emoji,
                fontSize = 32.sp
            )
            
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}
