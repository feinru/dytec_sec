package com.fein.dytec.presentation

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fein.dytec.Screen
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

sealed interface MainEvent {
    data class NavigateTo(val screen: Screen) : MainEvent
    data class NavigateToLesson(val lessonId: Int) : MainEvent
    data object NavigateBack : MainEvent
    data class SelectHomeTab(val tabIndex: Int) : MainEvent
    data class CompleteOnboarding(val name: String, val age: String, val tookDiagnostic: Boolean) : MainEvent
    data class UpdateThemeMode(val mode: ThemeMode) : MainEvent
    data class CompleteLesson(val nextLessonIdToUnlock: Int) : MainEvent
    data object ResetApp : MainEvent
}

data class MainState(
    val currentScreen: Screen = Screen.Login,
    val isOnboardingCompleted: Boolean = false,
    val selectedHomeTab: Int = 0,
    val userName: String = "",
    val userAge: String = "",
    val hasTakenDiagnostic: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val currentLessonId: Int? = null,
    val unlockedLessons: Set<String> = setOf("1")
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("dytec_prefs", Context.MODE_PRIVATE)
    
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val eventChannel = Channel<MainEvent>(Channel.BUFFERED)

    init {
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)
        val userName = sharedPreferences.getString("user_name", "") ?: ""
        val userAge = sharedPreferences.getString("user_age", "") ?: ""
        val hasTakenDiagnostic = sharedPreferences.getBoolean("has_taken_diagnostic", false)
        val themeModeStr = sharedPreferences.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        val themeMode = try { ThemeMode.valueOf(themeModeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        val unlockedLessons = sharedPreferences.getStringSet("unlocked_lessons", setOf("1")) ?: setOf("1")
        
        _state.update {
            it.copy(
                isOnboardingCompleted = isOnboardingCompleted,
                currentScreen = if (isOnboardingCompleted) Screen.Home else Screen.Login,
                userName = userName,
                userAge = userAge,
                hasTakenDiagnostic = hasTakenDiagnostic,
                themeMode = themeMode,
                unlockedLessons = unlockedLessons
            )
        }

        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: MainEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun handleEvent(event: MainEvent) {
        when (event) {
            is MainEvent.NavigateTo -> {
                _state.update { it.copy(currentScreen = event.screen) }
            }
            is MainEvent.NavigateToLesson -> {
                _state.update { it.copy(currentScreen = Screen.Lesson, currentLessonId = event.lessonId) }
            }
            is MainEvent.NavigateBack -> {
                val newScreen = when (_state.value.currentScreen) {
                    Screen.Name -> Screen.Login
                    Screen.Age -> Screen.Name
                    Screen.Anxiety -> Screen.Age
                    Screen.TestIntro -> Screen.Anxiety
                    Screen.Diagnostic -> if (_state.value.isOnboardingCompleted) Screen.Home else Screen.TestIntro
                    Screen.TestDetail -> Screen.Home
                    Screen.Lesson -> Screen.Home
                    Screen.ParentalGate -> Screen.Home
                    Screen.ParentMode -> Screen.Home
                    else -> _state.value.currentScreen
                }
                _state.update { it.copy(currentScreen = newScreen) }
            }
            is MainEvent.SelectHomeTab -> {
                _state.update { it.copy(selectedHomeTab = event.tabIndex) }
            }
            is MainEvent.CompleteLesson -> {
                val nextLessonId = event.nextLessonIdToUnlock
                val updatedUnlockedLessons = _state.value.unlockedLessons.toMutableSet().apply {
                    add(nextLessonId.toString())
                }
                
                sharedPreferences.edit()
                    .putStringSet("unlocked_lessons", updatedUnlockedLessons)
                    .apply()
                    
                _state.update { it.copy(unlockedLessons = updatedUnlockedLessons) }
            }
            is MainEvent.UpdateThemeMode -> {
                sharedPreferences.edit().putString("theme_mode", event.mode.name).apply()
                _state.update { it.copy(themeMode = event.mode) }
            }
            is MainEvent.CompleteOnboarding -> {
                sharedPreferences.edit()
                    .putBoolean("onboarding_completed", true)
                    .putString("user_name", event.name)
                    .putString("user_age", event.age)
                    .putBoolean("has_taken_diagnostic", event.tookDiagnostic)
                    .apply()
                _state.update { 
                    it.copy(
                        isOnboardingCompleted = true,
                        currentScreen = Screen.Home,
                        userName = event.name,
                        userAge = event.age,
                        hasTakenDiagnostic = event.tookDiagnostic
                    ) 
                }
            }
            is MainEvent.ResetApp -> {
                sharedPreferences.edit().clear().apply()
                _state.update {
                    it.copy(
                        isOnboardingCompleted = false,
                        currentScreen = Screen.Login,
                        selectedHomeTab = 0,
                        userName = "",
                        userAge = "",
                        hasTakenDiagnostic = false
                    )
                }
            }
        }
    }
}
