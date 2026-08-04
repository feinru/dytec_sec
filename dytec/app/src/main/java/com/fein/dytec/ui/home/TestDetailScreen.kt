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

@Composable
fun TestDetailScreen(
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
                    text = "Subtes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            val subtests = listOf(
                Pair("Waktu Reaksi", "410ms"),
                Pair("Hitung Titik", "Di Atas Rata-Rata"),
                Pair("Banding Angka", "Rata-Rata"),
                Pair("Penjumlahan", "Di Bawah Rata-Rata"),
                Pair("Perkalian", "Di Atas Rata-Rata"),
                Pair("Pengurangan", "Rata-Rata")
            )

            val colors = listOf(PrimaryBlue, PrimaryGreen, PrimaryBlue, PrimaryOrange, PrimaryGreen, PrimaryBlue)

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
