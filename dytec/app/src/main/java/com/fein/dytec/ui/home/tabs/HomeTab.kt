package com.fein.dytec.ui.home.tabs

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.theme.*
import kotlin.math.sin

data class LevelData(
    val id: Int,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val shadowColor: Color,
    val isLocked: Boolean,
    val isActive: Boolean
)

@Composable
fun HomeTab(
    hasTakenDiagnostic: Boolean = true,
    unlockedLessons: Set<String> = setOf("1"),
    onTakeDiagnostic: () -> Unit = {},
    onStartLesson: (Int) -> Unit = {}
) {
    var currentUnit by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(1) }
    val unitTitles = listOf("Menghitung Titik", "Compare Angka", "Pertambahan", "Perkalian")

    val levels = when (currentUnit) {
        1 -> listOf(
            LevelData(1, "Mengenal Titik", Icons.Filled.List, PrimaryBlue, PrimaryBlueShadow, !unlockedLessons.contains("1"), unlockedLessons.contains("1") && !unlockedLessons.contains("2")),
            LevelData(2, "Menghitung Banyak Titik", Icons.Filled.Star, PrimaryOrange, PrimaryOrangeShadow, !unlockedLessons.contains("2"), unlockedLessons.contains("2") && !unlockedLessons.contains("4")),
            LevelData(4, "Latihan Menghitung", Icons.Filled.Star, PrimaryGreen, PrimaryGreenShadow, !unlockedLessons.contains("4"), unlockedLessons.contains("4")),
        )
        else -> listOf(
            LevelData(10 * currentUnit + 1, "Materi Unit $currentUnit", Icons.Filled.List, PrimaryBlue, PrimaryBlueShadow, !unlockedLessons.contains("${10 * currentUnit + 1}"), unlockedLessons.contains("${10 * currentUnit + 1}") && !unlockedLessons.contains("${10 * currentUnit + 2}")),
            LevelData(10 * currentUnit + 2, "Latihan Unit $currentUnit", Icons.Filled.Build, PrimaryOrange, PrimaryOrangeShadow, !unlockedLessons.contains("${10 * currentUnit + 2}"), unlockedLessons.contains("${10 * currentUnit + 2}"))
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var showDiagnosticDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
        val pathColor = DytecTheme.colors.fieldBorder.copy(alpha = 0.8f)
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 240.dp, bottom = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            items(levels.size) { index ->
                val level = levels[index]
                val isLast = index == levels.size - 1
                
                val amplitude = 60f
                val period = 4f
                val offsetFraction = sin((index % period) / period * 2 * Math.PI).toFloat()
                val nextOffsetFraction = sin(((index + 1) % period) / period * 2 * Math.PI).toFloat()
                
                val currentOffsetDp = (offsetFraction * amplitude).dp
                val nextOffsetDp = (nextOffsetFraction * amplitude).dp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .zIndex(index.toFloat()),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isLast) {
                        val density = LocalDensity.current
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val startX = size.width / 2 + density.run { currentOffsetDp.toPx() }
                            val startY = size.height / 2
                            val endX = size.width / 2 + density.run { nextOffsetDp.toPx() }
                            val endY = size.height * 1.5f

                            val path = Path().apply {
                                moveTo(startX, startY)
                                cubicTo(
                                    startX, startY + size.height * 0.4f,
                                    endX, startY + size.height * 0.6f,
                                    endX, endY
                                )
                            }
                            
                            drawPath(
                                path = path,
                                color = pathColor,
                                style = Stroke(
                                    width = 16.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    Box(modifier = Modifier.offset(x = currentOffsetDp).zIndex(1f)) {
                        LevelNode(
                            modifier = Modifier.fillMaxSize(),
                            icon = level.icon,
                            color = level.color,
                            shadowColor = level.shadowColor,
                            isActive = level.isActive,
                            isLocked = level.isLocked,
                            title = level.title,
                            onClick = {
                                if (!hasTakenDiagnostic) {
                                    showDiagnosticDialog = true
                                } else if (!level.isLocked) {
                                    onStartLesson(level.id)
                                }
                            }
                        )
                    }
                }
            }
        }

        // Fixed Top Header Block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(DytecTheme.colors.bgWhite)
        ) {
            TopStatsBar()
            UnitHeader(
                unitTitle = "Unit $currentUnit",
                unitDescription = unitTitles[currentUnit - 1],
                backgroundColor = PrimaryGreen,
                shadowColor = PrimaryGreenShadow,
                onClick = { currentUnit = (currentUnit % 4) + 1 }
            )
        }
        
        if (showDiagnosticDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showDiagnosticDialog = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(DytecTheme.colors.fieldBorder) // 3D Shadow color
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(DytecTheme.colors.fieldBg)
                            .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tunggu Dulu!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DytecTheme.colors.textDark,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kamu harus mengambil Tes Diagnostik terlebih dahulu sebelum bisa memulai pelajaran.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DytecTheme.colors.textLight,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        com.fein.dytec.ui.login.components.DytecButton(
                            text = "MULAI TES",
                            onClick = {
                                showDiagnosticDialog = false
                                onTakeDiagnostic()
                            },
                            enabled = true,
                            color = PrimaryGreen,
                            shadowColor = PrimaryGreenShadow
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "NANTI SAJA",
                            color = PrimaryBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showDiagnosticDialog = false }
                                .padding(horizontal = 24.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
        }
    }
}


@Composable
fun UnitHeader(
    unitTitle: String,
    unitDescription: String,
    backgroundColor: Color,
    shadowColor: Color,
    icon: ImageVector = Icons.Filled.MenuBook,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(shadowColor)
            .padding(bottom = 6.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = unitTitle, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = unitDescription, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun TopStatsBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DytecTheme.colors.bgWhite)
            .padding(start = 16.dp, end = 16.dp, top = 48.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(icon = Icons.Filled.LocalFireDepartment, text = "1", color = PrimaryOrange)
        StatItem(icon = Icons.Filled.EmojiEvents, text = "12", color = Color(0xFFFFC107)) // Gold Trophies instead of Lives
    }
}

@Composable
fun StatItem(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun LevelNode(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    color: Color,
    shadowColor: Color,
    isActive: Boolean,
    isLocked: Boolean,
    title: String,
    onClick: () -> Unit = {}
) {
    val nodeColor = if (isLocked) DytecTheme.colors.fieldBorder else color
    val nodeShadowColor = if (isLocked) DytecTheme.colors.fieldBorder else shadowColor
    val iconColor = if (isLocked) Color(0xFFA0A0A0) else Color.White

    // Bouncing animation for active node
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isActive) -12f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            // Floating "START" Tooltip with chunky style
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-76).dp + bounceOffset.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(nodeShadowColor)
                        .clickable(enabled = !isLocked) { onClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(nodeColor)
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "MULAI", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
                // Triangle pointing down matching the shadow color
                Canvas(modifier = Modifier.size(width = 16.dp, height = 8.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2f, size.height)
                        close()
                    }
                    drawPath(path = path, color = nodeShadowColor)
                }
            }
        }

        // The Node Button
        Box(
            modifier = Modifier
                .size(width = 80.dp, height = 88.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(nodeShadowColor)
                .clickable(enabled = !isLocked) { onClick() },
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(nodeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(40.dp))
            }
        }
    }
}

