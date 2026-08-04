package com.fein.dytec.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.theme.*

@Composable
fun DiagnosticResultScreen(
    stanine: Int,
    score: Int,
    onContinue: () -> Unit
) {
    val (titleText, descText, color, shadowColor) = when {
        stanine >= 7 -> listOf("Luar Biasa!", "Kamu sangat hebat! Mari tingkatkan terus kemampuanmu bersama Dytec.", PrimaryGreen, PrimaryGreenShadow)
        stanine >= 4 -> listOf("Kerja Bagus!", "Kamu sudah berusaha dengan baik! Mari belajar matematika lebih seru lagi.", PrimaryBlue, PrimaryBlueShadow)
        else -> listOf("Tetap Semangat!", "Mari kita mulai petualangan seru untuk belajar matematika pelan-pelan bersama Dytec!", PrimaryOrange, PrimaryOrangeShadow)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = titleText as String,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color as androidx.compose.ui.graphics.Color
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = descText as String,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textLight,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(4.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stanine.toString(),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Text(
                    text = "Skor",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Kamu menjawab benar $score dari 5 soal",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        DytecButton(
            text = "MULAI PETUALANGAN",
            onClick = onContinue,
            color = color,
            shadowColor = shadowColor as androidx.compose.ui.graphics.Color
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
