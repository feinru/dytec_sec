package com.fein.dytec

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import com.fein.dytec.ui.login.LoginScreen
import com.fein.dytec.ui.onboarding.NameScreen
import com.fein.dytec.ui.theme.DytecTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.fein.dytec.ui.theme.PrimaryGreen
import com.fein.dytec.ui.onboarding.AgeScreen
import com.fein.dytec.ui.onboarding.AnxietyScreen
import com.fein.dytec.ui.onboarding.TestIntroScreen
import com.fein.dytec.ui.home.HomeScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fein.dytec.presentation.MainEvent
import com.fein.dytec.presentation.MainViewModel
import com.fein.dytec.presentation.onboarding.OnboardingViewModel

import com.fein.dytec.presentation.onboarding.DiagnosticViewModel
import com.fein.dytec.ui.onboarding.DiagnosticScreen
import com.fein.dytec.ui.onboarding.DiagnosticResultScreen

import com.fein.dytec.ui.home.ParentModeScreen
import com.fein.dytec.ui.home.ParentalGateScreen
import com.fein.dytec.ui.home.PsychologistListScreen
import com.fein.dytec.ui.home.PsychologistDetailScreen

enum class Screen { Login, Name, Age, Anxiety, TestIntro, Diagnostic, DiagnosticResult, Home, TestDetail, Lesson, ParentalGate, ParentMode, PsychologistList, PsychologistDetail }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsState()
            
            val onboardingViewModel: OnboardingViewModel = viewModel()
            val onboardingState by onboardingViewModel.state.collectAsState()
            
            val diagnosticViewModel: DiagnosticViewModel = viewModel()
            val diagnosticState by diagnosticViewModel.state.collectAsState()

            val isDarkTheme = when (state.themeMode) {
                com.fein.dytec.presentation.ThemeMode.LIGHT -> false
                com.fein.dytec.presentation.ThemeMode.DARK -> true
                com.fein.dytec.presentation.ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            DytecTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val currentScreen = state.currentScreen
                    
                    BackHandler(enabled = currentScreen in listOf(Screen.Name, Screen.Age, Screen.Anxiety, Screen.TestIntro, Screen.Diagnostic, Screen.TestDetail, Screen.Lesson, Screen.ParentalGate, Screen.ParentMode, Screen.PsychologistList, Screen.PsychologistDetail)) {
                        viewModel.onEvent(MainEvent.NavigateBack)
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DytecTheme.colors.bgWhite)
                    ) {
                        // Animated Progress Bar
                        AnimatedVisibility(
                            visible = currentScreen in listOf(Screen.Name, Screen.Age, Screen.Anxiety, Screen.TestIntro),
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            val progress = when (currentScreen) {
                                Screen.Name -> 0.25f
                                Screen.Age -> 0.50f
                                Screen.Anxiety -> 0.75f
                                Screen.TestIntro -> 1.0f
                                else -> 0f
                            }
                            
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy, 
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                        .clip(CircleShape)
                                        .background(DytecTheme.colors.fieldBorder) 
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedProgress.coerceAtLeast(0.05f))
                                        .height(14.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                )
                                // Optional 3D highlight
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(animatedProgress.coerceAtLeast(0.05f))
                                        .height(4.dp)
                                        .padding(start = 6.dp, end = 6.dp, top = 2.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
                            }
                        }
                        
                        AnimatedContent(
                            targetState = currentScreen,
                            modifier = Modifier.weight(1f),
                            transitionSpec = {
                                val isForward = targetState.ordinal > initialState.ordinal
                                slideInHorizontally(
                                    initialOffsetX = { if (isForward) it else -it },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                ) togetherWith slideOutHorizontally(
                                    targetOffsetX = { if (isForward) -it else it },
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            },
                            label = "screenTransition"
                        ) { targetScreen ->
                            when (targetScreen) {
                                Screen.Login -> {
                                    LoginScreen(
                                        onExitApp = {
                                            this@MainActivity.finish()
                                        },
                                        onGetStarted = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.Name))
                                        }
                                    )
                                }
                                Screen.Name -> {
                                    NameScreen(
                                        state = onboardingState,
                                        onEvent = onboardingViewModel::onEvent,
                                        onContinue = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.Age))
                                        },
                                        onBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.Age -> {
                                    AgeScreen(
                                        state = onboardingState,
                                        onEvent = onboardingViewModel::onEvent,
                                        onContinue = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.Anxiety))
                                        },
                                        onBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.Anxiety -> {
                                    AnxietyScreen(
                                        state = onboardingState,
                                        onEvent = onboardingViewModel::onEvent,
                                        onContinue = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.TestIntro))
                                        },
                                        onBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.TestIntro -> {
                                    TestIntroScreen(
                                        onStartTest = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.Diagnostic))
                                        },
                                        onSkipToHome = {
                                            viewModel.onEvent(MainEvent.CompleteOnboarding(
                                                name = onboardingState.name,
                                                age = onboardingState.age,
                                                tookDiagnostic = false
                                            ))
                                        },
                                        onBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.Diagnostic -> {
                                    LaunchedEffect(diagnosticState.isFinished) {
                                        if (diagnosticState.isFinished) {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.DiagnosticResult))
                                        }
                                    }
                                    DiagnosticScreen(
                                        state = diagnosticState,
                                        onAnswerSelected = diagnosticViewModel::selectAnswer,
                                        onSubmitAnswer = { isCorrect -> diagnosticViewModel.submitAnswer(isCorrect) },
                                        onRecordReaction = { ms -> diagnosticViewModel.recordReactionTime(ms) },
                                        onClose = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.DiagnosticResult -> {
                                    DiagnosticResultScreen(
                                        stanine = diagnosticState.finalStanine,
                                        score = diagnosticState.rawScore,
                                        onContinue = {
                                            viewModel.onEvent(MainEvent.CompleteOnboarding(
                                                name = onboardingState.name,
                                                age = onboardingState.age,
                                                tookDiagnostic = true
                                            ))
                                        }
                                    )
                                }
                                Screen.Home -> {
                                    HomeScreen(
                                        mainState = state,
                                        onEvent = viewModel::onEvent
                                    )
                                }
                                Screen.TestDetail -> {
                                    com.fein.dytec.ui.home.TestDetailScreen(
                                        onBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        }
                                    )
                                }
                                Screen.Lesson -> {
                                    com.fein.dytec.ui.lesson.LessonScreen(
                                        lessonId = state.currentLessonId ?: 1,
                                        onClose = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        },
                                        onNextLesson = { nextId ->
                                            viewModel.onEvent(MainEvent.NavigateToLesson(nextId))
                                        },
                                        onCompleteLesson = { nextLessonIdToUnlock ->
                                            viewModel.onEvent(MainEvent.CompleteLesson(nextLessonIdToUnlock))
                                        }
                                    )
                                }
                                Screen.ParentalGate -> {
                                    ParentalGateScreen(
                                        onNavigateBack = {
                                            viewModel.onEvent(MainEvent.NavigateBack)
                                        },
                                        onSuccess = {
                                            // Replace current ParentalGate with ParentMode
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.ParentMode))
                                        }
                                    )
                                }
                                Screen.ParentMode -> {
                                    ParentModeScreen(
                                        onNavigateBack = {
                                            // Make sure navigating back from ParentMode returns to Home/Stats
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.Home))
                                        },
                                        onOpenPsychologists = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.PsychologistList))
                                        }
                                    )
                                }
                                Screen.PsychologistList -> {
                                    PsychologistListScreen(
                                        onNavigateBack = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.ParentMode))
                                        },
                                        onOpenDetail = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.PsychologistDetail))
                                        }
                                    )
                                }
                                Screen.PsychologistDetail -> {
                                    PsychologistDetailScreen(
                                        onNavigateBack = {
                                            viewModel.onEvent(MainEvent.NavigateTo(Screen.PsychologistList))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}