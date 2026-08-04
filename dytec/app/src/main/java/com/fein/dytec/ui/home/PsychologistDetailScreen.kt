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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.login.components.DytecBackButton
import com.fein.dytec.ui.theme.*

@Composable
fun PsychologistDetailScreen(onNavigateBack: () -> Unit) {
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
                .padding(top = 48.dp, bottom = 100.dp)
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
                    text = "Konsultasi Diskalkulia",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Profile Card
                DuolingoCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Big Avatar
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Dr. Sarah Wijaya, M.Psi.",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DytecTheme.colors.textDark,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Spesialis Kesulitan Belajar & Diskalkulia",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryGreen
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4.9 (127 ulasan)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DytecTheme.colors.textLight
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tentang Psikolog Card
                DuolingoCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tentang Psikolog",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Psikolog berpengalaman lebih dari 8 tahun dalam menangani kesulitan belajar, khususnya diskalkulia. Lulusan S1 Psikologi dari Universitas Gadjah Mada. Telah membantu ratusan anak dan dewasa mengatasi kesulitan matematika dengan pendekatan terapi yang personal dan efektif.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DytecTheme.colors.textDark,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // Tags
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlineTag("Terapi Diskalkulia", PrimaryBlue)
                            OutlineTag("Asesmen Psikologi", PrimaryBlue)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlineTag("Konseling Keluarga", PrimaryBlue)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Jadwal Konsultasi Card
                DuolingoCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Jadwal Konsultasi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        ScheduleRow("Senin - Jumat", "09.00 - 17.00 WIB")
                        Spacer(modifier = Modifier.height(8.dp))
                        ScheduleRow("Sabtu", "09.00 - 15.00 WIB")
                        Spacer(modifier = Modifier.height(8.dp))
                        ScheduleRow("Minggu", "Tutup", isClosed = true)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Info box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Konsultasi dapat dilakukan secara online atau offline",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DytecTheme.colors.textDark
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tarif Konsultasi Card
                DuolingoCard {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AttachMoney,
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tarif Konsultasi",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryOrange
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PriceRow("Konsultasi Awal (90 menit)", "Rp 400.000")
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow("Konsultasi Lanjutan (60 menit)", "Rp 300.000")
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow("Asesmen Diskalkulia", "Rp 800.000")
                        Spacer(modifier = Modifier.height(8.dp))
                        PriceRow("Paket 4x Sesi Terapi", "Rp 1.000.000")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp)) // extra padding
            }
        }
        
        // Sticky Bottom Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DytecTheme.colors.bgWhite)
                .padding(24.dp)
        ) {
            DytecButton(
                text = "Hubungi via WhatsApp",
                onClick = {
                    val phoneNumber = "+6281234567890"
                    val message = "Halo Dr. Sarah, saya ingin berkonsultasi mengenai hasil tes diagnostik anak saya."
                    val uri = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {}
                },
                color = PrimaryGreen,
                shadowColor = PrimaryGreenShadow
            )
        }
    }
}

@Composable
fun DuolingoCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DytecTheme.colors.fieldBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-4).dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DytecTheme.colors.fieldBg)
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun OutlineTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun ScheduleRow(day: String, time: String, isClosed: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textDark
        )
        Text(
            text = time,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isClosed) PrimaryOrange else DytecTheme.colors.textDark
        )
    }
}

@Composable
fun PriceRow(service: String, price: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = service,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textDark,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = price,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DytecTheme.colors.textDark
        )
    }
}
