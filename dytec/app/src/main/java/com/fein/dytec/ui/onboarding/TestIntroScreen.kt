package com.fein.dytec.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.clickable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.theme.*

@Composable
fun TestIntroScreen(
    onStartTest: () -> Unit,
    onSkipToHome: () -> Unit,
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
                text = "Siap untuk tes?",
                fontSize = 32.sp,
                color = PrimaryGreen,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Kita akan bermain dengan angka di 3 misi:",
                fontSize = 16.sp,
                color = DytecTheme.colors.textLight,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Subtest Cards
            AnimatedVisibility(
                visible = isVisible,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { 200 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + androidx.compose.animation.fadeIn()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SubtestCard(
                        title = "Perhitungan Titik",
                        icon = "• • •",
                        mainColor = PrimaryBlue,
                        shadowColor = PrimaryBlueShadow
                    )
                    
                    SubtestCard(
                        title = "Perbandingan Angka",
                        icon = "< > =",
                        mainColor = PrimaryOrange,
                        shadowColor = PrimaryOrangeShadow
                    )
                    
                    SubtestCard(
                        title = "Operasi Aritmatika",
                        icon = "+ - ×",
                        mainColor = PrimaryRed,
                        shadowColor = PrimaryRedShadow
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1.5f))
        }
        
        // Bottom Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DytecButton(
                text = "MULAI PETUALANGAN!",
                onClick = onStartTest
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DytecButton(
                text = "MAIN KE BERANDA DULU",
                onClick = onSkipToHome,
                isSecondary = true
            )
        }
    }
}

@Composable
fun SubtestCard(
    title: String,
    icon: String,
    mainColor: Color,
    shadowColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(shadowColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(mainColor)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}
