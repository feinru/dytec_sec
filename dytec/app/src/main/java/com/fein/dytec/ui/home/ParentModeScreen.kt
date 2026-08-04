package com.fein.dytec.ui.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.login.components.DytecBackButton
import com.fein.dytec.ui.theme.*

@Composable
fun ParentModeScreen(onNavigateBack: () -> Unit, onOpenPsychologists: () -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 120.dp) // padding bottom for sticky button
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Playful 3D Duolingo Icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(PrimaryBlueShadow)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .offset(y = (-6).dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Mode Orang Tua",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark
                )
                Text(
                    text = "Area khusus untuk memantau perkembangan.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DytecTheme.colors.textLight,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                // Explanation Card (3D Duolingo design)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DytecTheme.colors.fieldBorder) // 3D shadow base
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-6).dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(DytecTheme.colors.fieldBg)
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 3D Icon Box
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryBlueShadow.copy(alpha = 0.25f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .offset(y = (-4).dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(PrimaryBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Skor Stanine",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DytecTheme.colors.textDark
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Penilaian berskala 1-9 untuk mengukur kemampuan secara akurat:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DytecTheme.colors.textLight,
                            lineHeight = 22.sp
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        StanineRow(label = "7 - 9", desc = "Di Atas Rata-Rata", color = PrimaryGreen, shadowColor = PrimaryGreenShadow)
                        Spacer(modifier = Modifier.height(12.dp))
                        StanineRow(label = "4 - 6", desc = "Rata-Rata", color = PrimaryBlue, shadowColor = PrimaryBlueShadow)
                        Spacer(modifier = Modifier.height(12.dp))
                        StanineRow(label = "1 - 3", desc = "Di Bawah Rata-Rata", color = PrimaryOrange, shadowColor = PrimaryOrangeShadow)
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Warning Box 3D
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryOrangeShadow.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = (-4).dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PrimaryOrange.copy(alpha = 0.15f))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Warning",
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Skor 1-3 yang konsisten bisa menjadi indikasi awal Diskalkulia (kesulitan belajar berhitung).",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryOrange,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Bottom Sticky Button (Already 3D implemented inside DytecButton)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DytecTheme.colors.bgWhite)
                .padding(24.dp)
        ) {
            DytecButton(
                text = "KONSULTASI PSIKOLOG",
                onClick = onOpenPsychologists,
                color = PrimaryGreen,
                shadowColor = PrimaryGreenShadow
            )
        }
    }
}

@Composable
fun StanineRow(label: String, desc: String, color: Color, shadowColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 3D Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(shadowColor)
        ) {
            Box(
                modifier = Modifier
                    .offset(y = (-4).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = desc,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}
