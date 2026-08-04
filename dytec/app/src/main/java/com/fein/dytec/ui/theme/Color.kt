package com.fein.dytec.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
// Playful Chunky Theme Colors
val PrimaryGreen = Color(0xFF58CC02)
val PrimaryGreenShadow = Color(0xFF58A700)

val PrimaryBlue = Color(0xFF1CB0F6)
val PrimaryBlueShadow = Color(0xFF1899D6)

val PrimaryOrange = Color(0xFFFF9600)
val PrimaryOrangeShadow = Color(0xFFCC7800)

val PrimaryRed = Color(0xFFFF4B4B)
val PrimaryRedShadow = Color(0xFFCC3C3C)

data class DytecColors(
    val bgWhite: Color,
    val fieldBg: Color,
    val fieldBorder: Color,
    val textDark: Color,
    val textLight: Color
)

val lightDytecColors = DytecColors(
    bgWhite = Color(0xFFFFFFFF),
    fieldBg = Color(0xFFF7F7F7),
    fieldBorder = Color(0xFFE5E5E5),
    textDark = Color(0xFF4B4B4B),
    textLight = Color(0xFFAFAFAF)
)

val darkDytecColors = DytecColors(
    bgWhite = Color(0xFF131415), // Very dark gray for main background
    fieldBg = Color(0xFF202224), // Slightly lighter for text fields/boxes
    fieldBorder = Color(0xFF35393B), // Dark borders
    textDark = Color(0xFFFFFFFF), // White text for primary
    textLight = Color(0xFF9E9E9E)  // Muted gray for secondary text
)