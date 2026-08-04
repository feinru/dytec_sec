package com.fein.dytec.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecBackButton
import com.fein.dytec.ui.theme.*

data class Psikolog(
    val name: String,
    val specialization: String,
    val phone: String,
    val experience: String,
    val rating: String,
    val price: String,
    val color: Color,
    val shadowColor: Color
)

@Composable
fun PsychologistListScreen(onNavigateBack: () -> Unit, onOpenDetail: () -> Unit) {
    val context = LocalContext.current

    val psikologList = listOf(
        Psikolog("Dr. Sarah Wijaya, M.Psi.", "Spesialis Kesulitan Belajar & Diskalkulia", "+6281234567890", "8 Tahun", "4.9", "Rp 400.000 / sesi", PrimaryGreen, PrimaryGreenShadow),
        Psikolog("Budi Santoso, M.Psi.", "Psikolog Anak & Remaja", "+6281234567891", "7 Tahun", "4.8", "Rp 300.000 / sesi", PrimaryBlue, PrimaryBlueShadow),
        Psikolog("Siti Aminah, M.Psi.", "Psikolog Klinis Anak", "+6281234567892", "5 Tahun", "4.7", "Rp 250.000 / sesi", PrimaryOrange, PrimaryOrangeShadow)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 48.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DytecBackButton(onClick = onNavigateBack)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Konsultasi Psikolog",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                psikologList.forEach { psikolog ->
                    PsikologCard(psikolog = psikolog, context = context, onClick = onOpenDetail)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PsikologCard(psikolog: Psikolog, context: android.content.Context, onClick: () -> Unit) {
    // 3D Duolingo Card
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DytecTheme.colors.fieldBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-6).dp)
                .clip(RoundedCornerShape(20.dp))
                .background(DytecTheme.colors.fieldBg)
                .clickable { onClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(psikolog.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = psikolog.color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = psikolog.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark
                )
                Text(
                    text = psikolog.specialization,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DytecTheme.colors.textLight
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Rating and Experience
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = psikolog.rating,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DytecTheme.colors.textDark
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Icon(
                        imageVector = Icons.Filled.Work,
                        contentDescription = "Pengalaman",
                        tint = DytecTheme.colors.textLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = psikolog.experience,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DytecTheme.colors.textLight
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Text(
                    text = psikolog.price,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = psikolog.color
                )
            }
        }
    }
}
