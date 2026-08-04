package com.fein.dytec.ui.home.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.fein.dytec.ui.theme.*
import com.fein.dytec.ui.login.components.DytecButton

@Composable
fun ProfileTab(
    userName: String,
    userAge: String,
    themeMode: com.fein.dytec.presentation.ThemeMode,
    onThemeChange: (com.fein.dytec.presentation.ThemeMode) -> Unit,
    onResetProgress: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(72.dp))

        // User Details
        val displayAge = userAge.takeIf { it.isNotBlank() } ?: "?"
        val displayName = userName.takeIf { it.isNotBlank() } ?: "Siswa"

        Text(
            text = displayName,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DytecTheme.colors.textDark
        )
        Text(
            text = "Usia: $displayAge tahun",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textLight
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Theme Selection
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Tampilan",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DytecTheme.colors.textDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            ThemeSegmentedControl(
                selectedMode = themeMode,
                onModeSelected = onThemeChange
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        // Feature 2: Pencapaian (Achievements)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Pencapaian",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DytecTheme.colors.textDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            AchievementItem(
                title = "Penantang Baru",
                description = "Selesaikan tes pertamamu",
                progress = 1,
                maxProgress = 1,
                isCompleted = true,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            AchievementItem(
                title = "Kutu Buku",
                description = "Belajar 3 hari berturut-turut",
                progress = 1,
                maxProgress = 3,
                isCompleted = false,
                color = PrimaryOrange
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Reset Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFE53935).copy(alpha = 0.2f)) // Red warning background
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Filled.Warning, contentDescription = null, tint = PrimaryRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zona Berbahaya",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryRed
                    )
                }
                
                DytecButton(
                    text = "HAPUS PROGRES",
                    onClick = onResetProgress,
                    enabled = true,
                    color = PrimaryRed,
                    shadowColor = Color(0xFFC62828)
                )
            }
        }
        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun ThemeSegmentedControl(
    selectedMode: com.fein.dytec.presentation.ThemeMode,
    onModeSelected: (com.fein.dytec.presentation.ThemeMode) -> Unit
) {
    val options = listOf(
        com.fein.dytec.presentation.ThemeMode.LIGHT to "Terang",
        com.fein.dytec.presentation.ThemeMode.SYSTEM to "Sistem",
        com.fein.dytec.presentation.ThemeMode.DARK to "Gelap"
    )
    
    val selectedIndex = options.indexOfFirst { it.first == selectedMode }.coerceAtLeast(0)
    
    androidx.compose.foundation.layout.BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DytecTheme.colors.fieldBg)
            .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / options.size
        
        val offset by androidx.compose.animation.core.animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = androidx.compose.animation.core.Spring.DampingRatioNoBouncy,
                stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
            ),
            label = "offset"
        )
        
        // Sliding indicator (3D style)
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .fillMaxHeight()
                .offset(x = offset)
                .padding(horizontal = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryGreenShadow)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen)
            )
        }
        
        // Options Row
        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, (mode, label) ->
                val isSelected = selectedMode == mode
                val textColor by androidx.compose.animation.animateColorAsState(
                    targetValue = if (isSelected) Color.White else DytecTheme.colors.textLight,
                    label = "textColor"
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            indication = null
                        ) { onModeSelected(mode) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementItem(title: String, description: String, progress: Int, maxProgress: Int, isCompleted: Boolean, color: Color) {
    val progressFraction = progress.toFloat() / maxProgress.toFloat()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DytecTheme.colors.fieldBorder) // shadow
            .padding(bottom = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DytecTheme.colors.fieldBg)
                .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) color.copy(alpha = 0.2f) else DytecTheme.colors.fieldBorder.copy(alpha = 0.2f))
                    .border(4.dp, if (isCompleted) color else DytecTheme.colors.fieldBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star, 
                    contentDescription = null, 
                    tint = if (isCompleted) color else DytecTheme.colors.fieldBorder,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (isCompleted) color else DytecTheme.colors.textLight)
                Text(text = description, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DytecTheme.colors.textLight, modifier = Modifier.padding(bottom = 8.dp))
                
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DytecTheme.colors.fieldBorder.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progressFraction)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}
