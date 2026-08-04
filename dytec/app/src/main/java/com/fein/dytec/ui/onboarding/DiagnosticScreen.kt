package com.fein.dytec.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fein.dytec.presentation.onboarding.DiagnosticState
import com.fein.dytec.presentation.onboarding.DiagnosticSubtest
import com.fein.dytec.ui.login.components.DytecButton
import com.fein.dytec.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun DiagnosticScreen(
    state: DiagnosticState,
    onAnswerSelected: (Int) -> Unit,
    onSubmitAnswer: (Boolean) -> Unit,
    onRecordReaction: (Long) -> Unit,
    onClose: () -> Unit
) {
    var hasSeenNotice by rememberSaveable { mutableStateOf(false) }
    var lastSeenIndex by rememberSaveable { mutableStateOf(state.currentSubtestIndex) }
    var isResting by rememberSaveable { mutableStateOf(false) }

    val currentSubtestIndex = state.currentSubtestIndex
    val totalSubtests = state.subtests.size

    LaunchedEffect(currentSubtestIndex) {
        if (currentSubtestIndex > lastSeenIndex) {
            lastSeenIndex = currentSubtestIndex
            if (currentSubtestIndex > 1 && currentSubtestIndex < totalSubtests) {
                isResting = true
            }
        }
    }

    if (state.isFinished) {
        DiagnosticResultScreen(
            stanine = state.finalStanine,
            score = state.rawScore,
            onContinue = onClose
        )
        return
    }

    val subtest = state.currentSubtest

    if (currentSubtestIndex == 1 && !hasSeenNotice) {
        SpeedNoticeScreen(
            onContinue = { hasSeenNotice = true }
        )
        return
    }

    if (isResting) {
        RestScreen(
            onContinue = { isResting = false }
        )
        return
    }

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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SUBTES ${subtest.id}/${state.subtests.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DytecTheme.colors.textDark
                )
                Text(
                    text = subtest.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = DytecTheme.colors.textLight
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Bar
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            val progressText = if (subtest is DiagnosticSubtest.ReactionTime) {
                "Percobaan ${subtest.currentTrial} dari ${subtest.totalTrials}"
            } else {
                "Soal 1 dari 1"
            }
            
            val progressFraction = if (subtest is DiagnosticSubtest.ReactionTime) {
                subtest.currentTrial.toFloat() / subtest.totalTrials.toFloat()
            } else {
                (state.currentSubtestIndex.toFloat() + 1) / state.subtests.size.toFloat()
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = progressText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DytecTheme.colors.textLight)
                Text(text = "${(progressFraction * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryGreen,
                trackColor = DytecTheme.colors.fieldBorder.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Subtest Content
        Box(modifier = Modifier.weight(1f)) {
            when (subtest) {
                is DiagnosticSubtest.ReactionTime -> {
                    ReactionTimeScreen(subtest = subtest, onRecordReaction = onRecordReaction)
                }
                is DiagnosticSubtest.DotCounting -> {
                    GenericMultipleChoiceScreen(
                        title = "Berapa jumlah titik di bawah?",
                        options = subtest.options,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = onAnswerSelected,
                        onSubmit = { onSubmitAnswer(state.selectedAnswerIndex == subtest.correctIndex) },
                        visualContent = { DotPattern() }
                    )
                }
                is DiagnosticSubtest.NumberComparison -> {
                    GenericMultipleChoiceScreen(
                        title = "Bilangan mana yang paling besar?",
                        options = subtest.options,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = onAnswerSelected,
                        onSubmit = { onSubmitAnswer(state.selectedAnswerIndex == subtest.correctIndex) },
                        visualContent = { 
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("9", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark)
                                Text("6", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark)
                                Text("2", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark)
                                Text("3", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark)
                            }
                        }
                    )
                }
                is DiagnosticSubtest.Addition -> {
                    GenericMultipleChoiceScreen(
                        title = "Berapa hasil dari operasi berikut?",
                        options = subtest.options,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = onAnswerSelected,
                        onSubmit = { onSubmitAnswer(state.selectedAnswerIndex == subtest.correctIndex) },
                        visualContent = { Text("1 + 6", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark) }
                    )
                }
                is DiagnosticSubtest.Multiplication -> {
                    GenericMultipleChoiceScreen(
                        title = "Berapa hasil dari operasi berikut?",
                        options = subtest.options,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = onAnswerSelected,
                        onSubmit = { onSubmitAnswer(state.selectedAnswerIndex == subtest.correctIndex) },
                        visualContent = { Text("3 × 5", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark) }
                    )
                }
                is DiagnosticSubtest.Subtraction -> {
                    GenericMultipleChoiceScreen(
                        title = "Berapa hasil dari operasi berikut?",
                        options = subtest.options,
                        selectedIndex = state.selectedAnswerIndex,
                        onSelect = onAnswerSelected,
                        onSubmit = { onSubmitAnswer(state.selectedAnswerIndex == subtest.correctIndex) },
                        visualContent = { Text("10 - 3", fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = DytecTheme.colors.textDark) }
                    )
                }
            }
        }
    }
}

enum class ReactionState { WAIT, READY, GO, DONE }

@Composable
fun ReactionTimeScreen(
    subtest: DiagnosticSubtest.ReactionTime,
    onRecordReaction: (Long) -> Unit
) {
    var state by remember(subtest.currentTrial) { mutableStateOf(ReactionState.WAIT) }
    var startTime by remember { mutableStateOf(0L) }
    var reactionTime by remember { mutableStateOf(0L) }
    
    LaunchedEffect(state) {
        when (state) {
            ReactionState.READY -> {
                val delayMs = (2000L..5000L).random()
                delay(delayMs)
                state = ReactionState.GO
                startTime = System.currentTimeMillis()
            }
            else -> {}
        }
    }

    val bgColor = when (state) {
        ReactionState.WAIT -> Color(0xFF4A4A4A)
        ReactionState.READY -> PrimaryRed
        ReactionState.GO -> PrimaryGreen
        ReactionState.DONE -> PrimaryBlue
    }

    val text = when (state) {
        ReactionState.WAIT -> "Ketuk untuk mulai"
        ReactionState.READY -> "Tunggu berwarna hijau..."
        ReactionState.GO -> "KETUK!"
        ReactionState.DONE -> "Bagus!\nKetuk untuk lanjut"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .pointerInput(state) {
                detectTapGestures {
                    when (state) {
                        ReactionState.WAIT -> { state = ReactionState.READY }
                        ReactionState.READY -> { 
                            state = ReactionState.WAIT 
                        }
                        ReactionState.GO -> {
                            reactionTime = System.currentTimeMillis() - startTime
                            state = ReactionState.DONE
                        }
                        ReactionState.DONE -> { 
                            onRecordReaction(reactionTime) 
                        } 
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DotPattern() {
    Box(modifier = Modifier.size(150.dp), contentAlignment = Alignment.Center) {
        val dots = listOf(
            Pair(0.2f, 0.2f), Pair(0.4f, 0.1f), Pair(0.7f, 0.3f), Pair(0.9f, 0.2f),
            Pair(0.1f, 0.5f), Pair(0.3f, 0.4f), Pair(0.5f, 0.6f), Pair(0.8f, 0.5f),
            Pair(0.2f, 0.8f), Pair(0.4f, 0.9f), Pair(0.6f, 0.8f), Pair(0.9f, 0.7f),
            Pair(0.5f, 0.3f), Pair(0.7f, 0.6f), Pair(0.8f, 0.9f), Pair(0.1f, 0.9f)
        )
        dots.forEach { (x, y) ->
            Box(
                modifier = Modifier
                    .offset(x = (150 * x).dp - 75.dp, y = (150 * y).dp - 75.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue)
            )
        }
    }
}

@Composable
fun GenericMultipleChoiceScreen(
    title: String,
    options: List<Int>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    visualContent: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DytecTheme.colors.fieldBg)
                .border(2.dp, DytecTheme.colors.fieldBorder, RoundedCornerShape(24.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DytecTheme.colors.textDark,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                visualContent()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Pilih jawaban yang benar dari pilihan di bawah ini:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DytecTheme.colors.textLight,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        options.forEachIndexed { index, option ->
            OptionCard(
                text = option.toString(),
                isSelected = selectedIndex == index,
                isCorrect = null,
                onClick = { onSelect(index) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        DytecButton(
            text = "Lanjutkan",
            onClick = onSubmit,
            enabled = selectedIndex != null,
            color = if (selectedIndex != null) PrimaryGreen else DytecTheme.colors.fieldBorder,
            shadowColor = if (selectedIndex != null) PrimaryGreenShadow else DytecTheme.colors.fieldBorder
        )
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

@Composable
fun SpeedNoticeScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Timer,
            contentDescription = null,
            tint = PrimaryOrange,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Satu Hal Lagi!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DytecTheme.colors.textDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Untuk pertanyaan berikutnya, usahakan untuk memilih jawaban secepat dan setepat mungkin ya!\n\nIngat, kamu tidak bisa kembali ke soal sebelumnya setelah menjawab.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        DytecButton(
            text = "SAYA MENGERTI",
            onClick = onContinue,
            color = PrimaryBlue,
            shadowColor = PrimaryBlueShadow
        )
    }
}

@Composable
fun RestScreen(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Istirahat Sejenak",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = DytecTheme.colors.textDark,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tarik napas panjang... \n\nSiap untuk soal selanjutnya?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DytecTheme.colors.textLight,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        DytecButton(
            text = "LANJUT",
            onClick = onContinue,
            color = PrimaryGreen,
            shadowColor = PrimaryGreenShadow
        )
    }
}
