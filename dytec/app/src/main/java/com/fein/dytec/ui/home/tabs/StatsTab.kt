package com.fein.dytec.ui.home.tabs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.theme.*

@Composable
fun StatsTab(
    history: List<com.fein.dytec.presentation.TestResultHistory>,
    onOpenTestDetail: (com.fein.dytec.presentation.TestResultHistory) -> Unit = {},
    onOpenParentMode: () -> Unit = {},
    onTakeDiagnostic: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Rapor Kamu",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryOrange
            )
            androidx.compose.material3.IconButton(onClick = onOpenParentMode) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Mode Orang Tua",
                    tint = DytecTheme.colors.textLight
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Riwayat Tes",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DytecTheme.colors.textDark
            )
            
            // Single Retest Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryBlueShadow)
                    .padding(bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryBlue)
                        .clickable { onTakeDiagnostic() }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Replay,
                        contentDescription = "Ulangi Tes",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ulangi Tes",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
        
        if (history.isEmpty()) {
            Text(
                text = "Belum ada riwayat tes",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            history.forEachIndexed { index, item ->
                val color = when (item.predictionLabel) {
                    "dyscalculia", "low_achievement" -> PrimaryOrange
                    "typical" -> PrimaryGreen
                    else -> PrimaryBlue
                }
                
                val scoreDisplay = when (item.predictionLabel) {
                    "dyscalculia" -> "Terindikasi Diskalkulia"
                    "low_achievement" -> "Pencapaian Rendah"
                    "typical" -> "Normal"
                    else -> item.predictionLabel
                }
                
                HistoryItem(
                    title = "Tes Diagnostik ${history.size - index}",
                    date = item.dateString,
                    score = scoreDisplay,
                    color = color,
                    onClick = { onOpenTestDetail(item) }
                )
            }
        }

        Spacer(modifier = Modifier.height(150.dp))
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    shadowColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DytecTheme.colors.fieldBorder)
            .padding(bottom = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DytecTheme.colors.fieldBg)
                .border(3.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DytecTheme.colors.textLight)
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color)
            }
        }
    }
}

@Composable
fun HistoryItem(
    title: String,
    date: String,
    score: String,
    color: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DytecTheme.colors.fieldBorder)
            .padding(bottom = 4.dp)
            .clickable { onClick() }
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .border(3.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DytecTheme.colors.textDark)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DytecTheme.colors.textLight)
                    Text(text = " • ", fontSize = 14.sp, color = DytecTheme.colors.textLight)
                    Text(text = score, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
                }
            }
            // Chevron Icon to indicate clickable
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Buka Detail",
                tint = DytecTheme.colors.textLight,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

