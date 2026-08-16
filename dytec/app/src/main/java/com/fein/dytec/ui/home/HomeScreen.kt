package com.fein.dytec.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fein.dytec.ui.theme.*
import com.fein.dytec.presentation.MainEvent
import com.fein.dytec.presentation.MainState
import com.fein.dytec.ui.home.tabs.HomeTab
import com.fein.dytec.ui.home.tabs.StatsTab
import com.fein.dytec.ui.home.tabs.ProfileTab

@Composable
fun HomeScreen(
    mainState: MainState,
    onEvent: (MainEvent) -> Unit,
    onStartDiagnosticTest: () -> Unit
) {
    val selectedTab = mainState.selectedHomeTab

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DytecTheme.colors.bgWhite)
    ) {
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                val isForward = targetState > initialState
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
            label = "tab_transition"
        ) { targetTab ->
            when (targetTab) {
                0 -> {
                    HomeTab(
                        hasTakenDiagnostic = mainState.hasTakenDiagnostic,
                        unlockedLessons = mainState.unlockedLessons,
                        onTakeDiagnostic = onStartDiagnosticTest,
                        onStartLesson = { lessonId -> onEvent(MainEvent.NavigateToLesson(lessonId)) }
                    )
                }
                1 -> {
                    StatsTab(
                        history = mainState.testHistory,
                        onOpenTestDetail = { historyItem ->
                            onEvent(MainEvent.NavigateToTestDetail(historyItem))
                        },
                        onOpenParentMode = {
                            onEvent(MainEvent.NavigateTo(com.fein.dytec.Screen.ParentalGate))
                        },
                        onTakeDiagnostic = onStartDiagnosticTest
                    )
                }
                2 -> {
                    ProfileTab(
                        userName = mainState.userName,
                        userAge = mainState.userAge,
                        themeMode = mainState.themeMode,
                        onThemeChange = { onEvent(MainEvent.UpdateThemeMode(it)) },
                        onResetProgress = {
                            onEvent(MainEvent.ResetApp)
                        }
                    )
                }
            }
        }

        // Bottom Nav Bar
        BottomNavBar(
            selectedTab = selectedTab,
            onTabSelected = { onEvent(MainEvent.SelectHomeTab(it)) },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        DytecTheme.colors.bgWhite.copy(alpha = 0.8f),
                        DytecTheme.colors.bgWhite
                    )
                )
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = Icons.Filled.Home, isSelected = selectedTab == 0, onClick = { onTabSelected(0) }, color = PrimaryBlue, shadowColor = PrimaryBlueShadow)
            BottomNavItem(icon = Icons.Filled.Assessment, isSelected = selectedTab == 1, onClick = { onTabSelected(1) }, color = PrimaryOrange, shadowColor = PrimaryOrangeShadow)
            BottomNavItem(icon = Icons.Filled.Person, isSelected = selectedTab == 2, onClick = { onTabSelected(2) }, color = PrimaryGreen, shadowColor = PrimaryGreenShadow)
        }
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit, color: Color, shadowColor: Color) {
    val animatedHorizontalPadding by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 16.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "padding_x"
    )
    val animatedVerticalPadding by animateDpAsState(
        targetValue = if (isSelected) 14.dp else 16.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "padding_y"
    )
    val animatedShadowPadding by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "shadow_padding"
    )
    val animatedBgColor by animateColorAsState(targetValue = if (isSelected) color else Color.Transparent, label = "bg_color")
    val animatedIconColor by animateColorAsState(targetValue = if (isSelected) Color.White else Color.Gray, label = "icon_color")
    val animatedShadowColor by animateColorAsState(targetValue = if (isSelected) shadowColor else Color.Transparent, label = "shadow_color")

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(animatedShadowColor)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = animatedShadowPadding.coerceAtLeast(0.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(animatedBgColor)
                .padding(horizontal = animatedHorizontalPadding.coerceAtLeast(0.dp), vertical = animatedVerticalPadding.coerceAtLeast(0.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = animatedIconColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
