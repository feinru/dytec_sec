package com.fein.dytec.ui.lesson

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.theme.*
import com.fein.dytec.ui.login.components.DytecButton

@Composable
fun LessonScreen(
    lessonId: Int = 1,
    onClose: () -> Unit,
    onNextLesson: ((Int) -> Unit)? = null,
    onCompleteLesson: (Int) -> Unit = {}
) {
    var currentStep by remember(lessonId) { mutableStateOf(0) }
    var dot1Tapped by remember(lessonId) { mutableStateOf(false) }
    var dot2Tapped by remember(lessonId) { mutableStateOf(false) }
    val allTapped = dot1Tapped && dot2Tapped
    
    // States for Lesson 2
    var l2S0D1 by remember(lessonId) { mutableStateOf(false) }
    var l2S0D2 by remember(lessonId) { mutableStateOf(false) }
    var l2S0D3 by remember(lessonId) { mutableStateOf(false) }
    val l2S0Done = l2S0D1 && l2S0D2 && l2S0D3

    var l2S1D1 by remember(lessonId) { mutableStateOf(false) }
    var l2S1D2 by remember(lessonId) { mutableStateOf(false) }
    var l2S1D3 by remember(lessonId) { mutableStateOf(false) }
    var l2S1D4 by remember(lessonId) { mutableStateOf(false) }
    val l2S1Done = l2S1D1 && l2S1D2 && l2S1D3 && l2S1D4
    var isCorrect by remember(lessonId) { mutableStateOf<Boolean?>(null) }
    var isSubmitted by remember(lessonId) { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Tutup",
                tint = DytecTheme.colors.textLight,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onClose() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Progress Bar
            val targetProgress = when (lessonId) {
                1 -> if (currentStep == 0) 0.3f else if (allTapped) 1f else 0.7f
                2 -> if (currentStep == 0) (if (l2S0Done) 0.5f else 0.2f) else (if (l2S1Done) 1f else 0.8f)
                4 -> if (isCorrect == true) 1f else 0.5f
                else -> 0.5f
            }
            val progressFraction by animateFloatAsState(targetValue = targetProgress)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(16.dp)
                    .clip(CircleShape)
                    .background(DytecTheme.colors.fieldBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(16.dp)
                        .clip(CircleShape)
                        .background(PrimaryGreen)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressFraction)
                        .height(6.dp)
                        .padding(start = 8.dp, end = 8.dp, top = 3.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
            
            Spacer(modifier = Modifier.width(32.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (lessonId == 1) {
            if (currentStep == 0) {
                // LESSON MODE (1 Dot)
                Text(
                    text = "Mari berkenalan dengan titik!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Ini adalah 1 titik.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DytecTheme.colors.textLight,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Visual Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(DytecTheme.colors.fieldBg)
                        .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom Bar
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DytecButton(
                        text = "MENGERTI",
                        onClick = { currentStep = 1 },
                        enabled = true,
                        color = PrimaryBlue,
                        shadowColor = PrimaryBlueShadow
                    )
                }
            } else {
                // INTERACTIVE COUNTING MODE (2 Dots)
                Text(
                    text = "Mari Menghitung! Sentuh semua titik biru di bawah ini.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Visual Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (allTapped) PrimaryGreen.copy(alpha = 0.1f) else DytecTheme.colors.fieldBg)
                        .border(2.dp, if (allTapped) PrimaryGreen else DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (dot1Tapped) PrimaryGreen else PrimaryBlue)
                                .clickable(enabled = !dot1Tapped) {
                                    dot1Tapped = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (dot1Tapped) {
                                Text("1", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (dot2Tapped) PrimaryGreen else PrimaryBlue)
                                .clickable(enabled = !dot2Tapped) {
                                    dot2Tapped = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (dot2Tapped) {
                                Text("2", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
                
                if (allTapped) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Hebat! Itu adalah 2 titik.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Bottom Bar
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (allTapped) {
                        DytecButton(
                            text = "LANJUT KE BAB 2",
                            onClick = { 
                                onCompleteLesson(2) // Unlock Node 2
                                onNextLesson?.invoke(2) 
                            },
                            enabled = true,
                            color = PrimaryGreen,
                            shadowColor = PrimaryGreenShadow
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    DytecButton(
                        text = "SELESAI",
                        onClick = { 
                            if (allTapped) onCompleteLesson(2) // Unlock Node 2
                            onClose() 
                        },
                        enabled = allTapped,
                        color = if (allTapped) DytecTheme.colors.fieldBorder else DytecTheme.colors.fieldBorder,
                        shadowColor = DytecTheme.colors.fieldBorder
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Kembali ke materi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { 
                                currentStep = 0 
                                dot1Tapped = false
                                dot2Tapped = false
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        } else if (lessonId == 2) {
            if (currentStep == 0) {
                // 3 DOTS
                Text(
                    text = "Mari Menghitung 3 Titik! Sentuh semua titik biru.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Visual Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (l2S0Done) PrimaryGreen.copy(alpha = 0.1f) else DytecTheme.colors.fieldBg)
                        .border(2.dp, if (l2S0Done) PrimaryGreen else DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(70.dp).clip(CircleShape).background(if (l2S0D1) PrimaryGreen else PrimaryBlue)
                                .clickable(enabled = !l2S0D1) { l2S0D1 = true },
                            contentAlignment = Alignment.Center
                        ) { if (l2S0D1) Text("1", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold) }
                        
                        Box(
                            modifier = Modifier.size(70.dp).clip(CircleShape).background(if (l2S0D2) PrimaryGreen else PrimaryBlue)
                                .clickable(enabled = !l2S0D2) { l2S0D2 = true },
                            contentAlignment = Alignment.Center
                        ) { if (l2S0D2) Text("2", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold) }
                        
                        Box(
                            modifier = Modifier.size(70.dp).clip(CircleShape).background(if (l2S0D3) PrimaryGreen else PrimaryBlue)
                                .clickable(enabled = !l2S0D3) { l2S0D3 = true },
                            contentAlignment = Alignment.Center
                        ) { if (l2S0D3) Text("3", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold) }
                    }
                }
                
                if (l2S0Done) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Bagus! Itu adalah 3 titik.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    DytecButton(
                        text = "LANJUT KE 4 TITIK",
                        onClick = { currentStep = 1 },
                        enabled = l2S0Done,
                        color = if (l2S0Done) PrimaryBlue else DytecTheme.colors.fieldBorder,
                        shadowColor = if (l2S0Done) PrimaryBlueShadow else DytecTheme.colors.fieldBorder
                    )
                }
            } else {
                // 4 DOTS
                Text(
                    text = "Sekarang Mari Menghitung 4 Titik!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Visual Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(if (l2S1Done) PrimaryGreen.copy(alpha = 0.1f) else DytecTheme.colors.fieldBg)
                        .border(2.dp, if (l2S1Done) PrimaryGreen else DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // We'll use a 2x2 grid for 4 dots
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(
                                modifier = Modifier.size(60.dp).clip(CircleShape).background(if (l2S1D1) PrimaryGreen else PrimaryBlue)
                                    .clickable(enabled = !l2S1D1) { l2S1D1 = true },
                                contentAlignment = Alignment.Center
                            ) { if (l2S1D1) Text("1", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
                            
                            Box(
                                modifier = Modifier.size(60.dp).clip(CircleShape).background(if (l2S1D2) PrimaryGreen else PrimaryBlue)
                                    .clickable(enabled = !l2S1D2) { l2S1D2 = true },
                                contentAlignment = Alignment.Center
                            ) { if (l2S1D2) Text("2", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(
                                modifier = Modifier.size(60.dp).clip(CircleShape).background(if (l2S1D3) PrimaryGreen else PrimaryBlue)
                                    .clickable(enabled = !l2S1D3) { l2S1D3 = true },
                                contentAlignment = Alignment.Center
                            ) { if (l2S1D3) Text("3", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
                            
                            Box(
                                modifier = Modifier.size(60.dp).clip(CircleShape).background(if (l2S1D4) PrimaryGreen else PrimaryBlue)
                                    .clickable(enabled = !l2S1D4) { l2S1D4 = true },
                                contentAlignment = Alignment.Center
                            ) { if (l2S1D4) Text("4", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold) }
                        }
                    }
                }
                
                if (l2S1Done) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Luar biasa! Itu adalah 4 titik.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Column(modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (l2S1Done) {
                        DytecButton(
                            text = "LANJUT KE LATIHAN",
                            onClick = { 
                                onCompleteLesson(4) // Unlock Node 4
                                onNextLesson?.invoke(4) 
                            },
                            enabled = true,
                            color = PrimaryGreen,
                            shadowColor = PrimaryGreenShadow
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    DytecButton(
                        text = "SELESAI",
                        onClick = { 
                            if (l2S1Done) onCompleteLesson(4) // Unlock Node 4
                            onClose() 
                        },
                        enabled = l2S1Done,
                        color = if (l2S1Done) DytecTheme.colors.fieldBorder else DytecTheme.colors.fieldBorder,
                        shadowColor = DytecTheme.colors.fieldBorder
                    )
                }
            }
        } else if (lessonId == 4) {
            // LATIHAN MENGHITUNG (Quiz Mode)
            var selectedOption by remember(lessonId) { mutableStateOf<Int?>(null) }
            
            Text(
                text = "Ada berapa titik biru di bawah ini?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DytecTheme.colors.textDark,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Visual Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DytecTheme.colors.fieldBg)
                    .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlue)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Options Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OptionCard(
                        text = "1",
                        isSelected = selectedOption == 1,
                        isCorrect = if (isSubmitted && selectedOption == 1) false else null,
                        onClick = { 
                            selectedOption = 1
                            isSubmitted = false
                            isCorrect = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OptionCard(
                        text = "2",
                        isSelected = selectedOption == 2,
                        isCorrect = if (isSubmitted && selectedOption == 2) false else null,
                        onClick = { 
                            selectedOption = 2
                            isSubmitted = false
                            isCorrect = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OptionCard(
                        text = "3",
                        isSelected = selectedOption == 3,
                        isCorrect = if (isSubmitted && selectedOption == 3) true else null,
                        onClick = { 
                            selectedOption = 3
                            isSubmitted = false
                            isCorrect = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                    OptionCard(
                        text = "4",
                        isSelected = selectedOption == 4,
                        isCorrect = if (isSubmitted && selectedOption == 4) false else null,
                        onClick = { 
                            selectedOption = 4
                            isSubmitted = false
                            isCorrect = false
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            if (isSubmitted && selectedOption != 3 && selectedOption != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryRed.copy(alpha = 0.1f))
                        .border(2.dp, PrimaryRed, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ini adalah jumlah titik yang kamu pilih:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(selectedOption!!) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryRed)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Coba bandingkan dengan titik di atas.",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DytecTheme.colors.textDark,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bottom Bar
            Column(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isDone = isSubmitted && selectedOption == 3
                val isWrong = isSubmitted && selectedOption != 3
                
                DytecButton(
                    text = if (isDone) "SELESAI" else if (isWrong) "COBA LAGI" else "PILIH JAWABAN",
                    onClick = { 
                        if (isDone) {
                            onCompleteLesson(21)
                            onClose()
                        } else if (isWrong) {
                            isSubmitted = false
                            selectedOption = null
                            isCorrect = null
                        } else {
                            isSubmitted = true
                            isCorrect = selectedOption == 3
                        }
                    },
                    enabled = selectedOption != null || isWrong,
                    color = if (isDone) PrimaryGreen else if (isWrong) PrimaryOrange else if (selectedOption != null) PrimaryBlue else DytecTheme.colors.fieldBorder,
                    shadowColor = if (isDone) PrimaryGreenShadow else if (isWrong) PrimaryOrangeShadow else if (selectedOption != null) PrimaryBlueShadow else DytecTheme.colors.fieldBorder
                )
            }
        } else {
            // Fallback for unimplemented nodes (like node 3 and 4)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Materi ini belum tersedia.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DytecTheme.colors.textDark)
            }
        }
    }
}

@Composable
fun OptionCard(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultBorderColor = DytecTheme.colors.fieldBorder
    val defaultBgColor = DytecTheme.colors.bgWhite
    val defaultShadowColor = DytecTheme.colors.fieldBorder
    
    val borderColor = if (isSelected) {
        when (isCorrect) {
            true -> PrimaryGreen
            false -> PrimaryRed
            null -> PrimaryBlue
        }
    } else defaultBorderColor
    
    val bgColor = if (isSelected) {
        when (isCorrect) {
            true -> PrimaryGreen.copy(alpha = 0.1f)
            false -> PrimaryRed.copy(alpha = 0.1f)
            null -> PrimaryBlue.copy(alpha = 0.1f)
        }
    } else defaultBgColor
    
    val shadowColor = if (isSelected) {
        when (isCorrect) {
            true -> PrimaryGreenShadow
            false -> PrimaryRedShadow
            null -> PrimaryBlue.copy(alpha = 0.3f)
        }
    } else defaultShadowColor
    
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(shadowColor)
            .padding(bottom = if (isSelected) 2.dp else 4.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isSelected) {
                    when (isCorrect) {
                        true -> PrimaryGreen
                        false -> PrimaryRed
                        null -> PrimaryBlue
                    }
                } else DytecTheme.colors.textDark,
                textAlign = TextAlign.Center
            )
        }
    }
}
