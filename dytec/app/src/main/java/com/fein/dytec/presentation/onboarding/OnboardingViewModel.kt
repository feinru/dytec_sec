package com.fein.dytec.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface OnboardingEvent {
    data class NameChanged(val name: String) : OnboardingEvent
    data class AgeChanged(val age: String) : OnboardingEvent
    data class EmotionSelected(val emotion: String) : OnboardingEvent
}

data class OnboardingState(
    val name: String = "",
    val isNameValid: Boolean = false,
    val age: String = "",
    val isAgeValid: Boolean = false,
    val selectedEmotion: String? = null,
    val isLoading: Boolean = false
)

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val eventChannel = Channel<OnboardingEvent>(Channel.BUFFERED)

    init {
        viewModelScope.launch {
            eventChannel.receiveAsFlow().collect { event ->
                handleEvent(event)
            }
        }
    }

    fun onEvent(event: OnboardingEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    private fun handleEvent(event: OnboardingEvent) {
        when (event) {
            is OnboardingEvent.NameChanged -> {
                _state.update {
                    it.copy(
                        name = event.name,
                        isNameValid = event.name.isNotBlank() && event.name.length >= 2
                    )
                }
            }
            is OnboardingEvent.AgeChanged -> {
                _state.update {
                    it.copy(
                        age = event.age,
                        isAgeValid = event.age.isNotBlank() && (event.age.toIntOrNull() ?: 0) > 0
                    )
                }
            }
            is OnboardingEvent.EmotionSelected -> {
                _state.update {
                    it.copy(
                        selectedEmotion = event.emotion
                    )
                }
            }
        }
    }
}
