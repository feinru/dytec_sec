package com.fein.dytec.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecBackButton
import com.fein.dytec.ui.theme.*

import com.fein.dytec.presentation.TestResultHistory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

@Composable
fun TestDetailScreen(
    historyItem: TestResultHistory,
    onBack: () -> Unit
) {
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
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DytecBackButton(onClick = onBack)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Detail Tes",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = DytecTheme.colors.textDark
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Hasil Analisis Model ML",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DytecTheme.colors.fieldBg),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DytecTheme.colors.fieldBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Prediksi Diagnosis:",
                            fontSize = 14.sp,
                            color = DytecTheme.colors.textDark.copy(alpha = 0.7f)
                        )
                        val predictionText = historyItem.predictionLabel
                        val predictionColor = when (predictionText) {
                            "dyscalculia", "low_achievement" -> PrimaryOrange
                            "typical" -> PrimaryGreen
                            else -> Color.Gray
                        }
                        val predictionDisplay = when (predictionText) {
                            "dyscalculia" -> "Terindikasi Diskalkulia"
                            "low_achievement" -> "Pencapaian Rendah (Beresiko)"
                            "typical" -> "Normal"
                            else -> "Belum Ada Data ($predictionText)"
                        }
                        
                        Text(
                            text = predictionDisplay,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = predictionColor,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        
                        Text(
                            text = "Probabilitas Model:",
                            fontSize = 14.sp,
                            color = DytecTheme.colors.textDark.copy(alpha = 0.7f)
                        )
                        if (historyItem.predictionProbabilities.isNotEmpty()) {
                            historyItem.predictionProbabilities.forEach { (label, prob) ->
                                val className = when (label) {
                                    "dyscalculia" -> "Diskalkulia"
                                    "low_achievement" -> "Pencapaian Rendah"
                                    "typical" -> "Normal"
                                    else -> label.replace("_", " ").replaceFirstChar { it.uppercase() }
                                }
                                Text(
                                    text = "- $className: ${(prob * 100).toInt()}%",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DytecTheme.colors.textDark
                                )
                            }
                        } else {
                            Text(
                                text = "Tidak ada data probabilitas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = DytecTheme.colors.textDark
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subtes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val avgRt = if (historyItem.avgRt < 10000) "${historyItem.avgRt.toInt()}ms" else "N/A"

            val finalScore = historyItem.finalScore
            val subtests = listOf(
                Pair("Waktu Reaksi (Real)", avgRt),
                Pair("Total Skor Benar", "$finalScore / 5"),
                Pair("Input: Umur", "${historyItem.userAge} Tahun"),
                Pair("Input: Dot Enum", "${70.0 + (finalScore * 5)}"),
                Pair("Input: Stroop", "${75.0 + (finalScore * 4)}"),
                Pair("Input: Penjumlahan", "${80.0 + (finalScore * 3)}"),
                Pair("Input: Perkalian", "85.0")
            )

            val colors = listOf(PrimaryBlue, PrimaryGreen, PrimaryGreen, PrimaryBlue, PrimaryOrange, PrimaryGreen, PrimaryBlue)

            items(subtests.size) { index ->
                SubtestItem(
                    title = subtests[index].first,
                    score = subtests[index].second,
                    color = colors[index]
                )
            }
        }
    }
}

@Composable
fun SubtestItem(
    title: String,
    score: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DytecTheme.colors.fieldBorder)
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
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DytecTheme.colors.textDark,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = score,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}
