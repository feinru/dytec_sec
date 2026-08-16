package com.fein.dytec.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.fein.dytec.data.remote.RetrofitClient
import com.fein.dytec.data.remote.models.PredictionRequest
import com.fein.dytec.data.remote.models.TokenRequest
import android.util.Log

sealed class DiagnosticSubtest(val id: Int, val title: String) {
    data class ReactionTime(val totalTrials: Int = 3, val currentTrial: Int = 1, val reactionTimesMs: List<Long> = emptyList()) : DiagnosticSubtest(1, "Waktu Reaksi")
    data class DotCounting(val options: List<Int> = listOf(12, 16, 19, 7), val correctIndex: Int = 1) : DiagnosticSubtest(2, "Menghitung Titik")
    data class NumberComparison(val options: List<Int> = listOf(2, 9, 3, 6), val correctIndex: Int = 1) : DiagnosticSubtest(3, "Membandingkan Angka")
    data class Addition(val options: List<Int> = listOf(8, 5, 9, 7), val correctIndex: Int = 2) : DiagnosticSubtest(4, "Penjumlahan")
    data class Multiplication(val options: List<Int> = listOf(12, 15, 10, 20), val correctIndex: Int = 1) : DiagnosticSubtest(5, "Perkalian")
    data class Subtraction(val options: List<Int> = listOf(6, 7, 8, 5), val correctIndex: Int = 1) : DiagnosticSubtest(6, "Pengurangan")
}

data class DiagnosticState(
    val subtests: List<DiagnosticSubtest> = listOf(
        DiagnosticSubtest.ReactionTime(),
        DiagnosticSubtest.DotCounting(),
        DiagnosticSubtest.NumberComparison(),
        DiagnosticSubtest.Addition(),
        DiagnosticSubtest.Subtraction(),
        DiagnosticSubtest.Multiplication(),
    ),
    val currentSubtestIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val rawScore: Int = 0,
    val isFinished: Boolean = false,
    val finalStanine: Int = 0,
    val timeElapsedSeconds: Int = 0,
    val userAge: Double = 8.5,
    val predictionLabel: String? = null,
    val predictionProbabilities: Map<String, Double> = emptyMap(),
    val isApiLoading: Boolean = false,
    val apiError: String? = null,
    val isSaved: Boolean = false
) {
    val currentSubtest: DiagnosticSubtest
        get() = subtests[currentSubtestIndex]
        
    val progress: Float
        get() = currentSubtestIndex.toFloat() / subtests.size.toFloat()
}

class DiagnosticViewModel : ViewModel() {

    private val _state = MutableStateFlow(DiagnosticState())
    val state: StateFlow<DiagnosticState> = _state.asStateFlow()

    private var timerJob: kotlinx.coroutines.Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                if (_state.value.isFinished) break
                delay(1000)
                _state.update { it.copy(timeElapsedSeconds = it.timeElapsedSeconds + 1) }
            }
        }
    }

    fun reset(age: Double = 8.5) {
        _state.value = DiagnosticState(userAge = age)
        startTimer()
    }
    
    fun markAsSaved() {
        _state.update { it.copy(isSaved = true) }
    }

    fun selectAnswer(index: Int) {
        _state.update { it.copy(selectedAnswerIndex = index) }
    }

    fun recordReactionTime(timeMs: Long) {
        val currentState = _state.value
        val subtest = currentState.currentSubtest as? DiagnosticSubtest.ReactionTime ?: return
        
        val newList = subtest.reactionTimesMs + timeMs
        
        if (subtest.currentTrial < subtest.totalTrials) {
            val newSubtest = subtest.copy(
                currentTrial = subtest.currentTrial + 1,
                reactionTimesMs = newList
            )
            val newSubtests = currentState.subtests.toMutableList()
            newSubtests[currentState.currentSubtestIndex] = newSubtest
            _state.update { it.copy(subtests = newSubtests) }
        } else {
            val newSubtest = subtest.copy(reactionTimesMs = newList)
            val newSubtests = currentState.subtests.toMutableList()
            newSubtests[currentState.currentSubtestIndex] = newSubtest
            _state.update { it.copy(subtests = newSubtests) }
            
            submitAnswer(isCorrect = false)
        }
    }

    fun submitAnswer(isCorrect: Boolean = false) {
        val currentState = _state.value
        val newScore = if (isCorrect) currentState.rawScore + 1 else currentState.rawScore
        
        if (currentState.currentSubtestIndex < currentState.subtests.size - 1) {
            _state.update { 
                it.copy(
                    currentSubtestIndex = it.currentSubtestIndex + 1,
                    selectedAnswerIndex = null,
                    rawScore = newScore
                ) 
            }
        } else {
            finishTest(newScore)
        }
    }

    private fun finishTest(finalScore: Int) {
        val subtests = _state.value.subtests
        val rtSubtest = subtests[0] as DiagnosticSubtest.ReactionTime
        val avgRt = if (rtSubtest.reactionTimesMs.isNotEmpty()) rtSubtest.reactionTimesMs.average() else 1000.0
        
        var stanine = when (finalScore) {
            0 -> 1
            1 -> 2
            2 -> 3
            3 -> 4
            4 -> 5
            5 -> 7
            else -> 1
        }
        
        if (avgRt < 400) stanine += 2
        else if (avgRt < 600) stanine += 1
        
        if (stanine > 9) stanine = 9
        
        _state.update {
            it.copy(
                isFinished = true,
                finalStanine = stanine,
                rawScore = finalScore,
                isApiLoading = true
            )
        }
        
        viewModelScope.launch {
            try {
                // Step 1: Get Token
                val tokenReq = TokenRequest("dytec_android", "dummy_secret")
                val tokenResponse = RetrofitClient.api.getToken(tokenReq)
                
                if (tokenResponse.isSuccessful) {
                    val token = tokenResponse.body()?.access_token
                    RetrofitClient.authInterceptor.token = token
                    
                    // Step 2: Prepare Prediction Request (using dummy standard scores mapped from raw)
                    // In a real app, you would calculate actual standard scores here
                    val predictionReq = PredictionRequest(
                        age = _state.value.userAge,
                        std_dot_enum = 70.0 + (finalScore * 5),
                        std_stroop = 75.0 + (finalScore * 4),
                        std_addition = 80.0 + (finalScore * 3),
                        std_multiplication = 85.0,
                        has_mult = 1
                    )
                    
                    val predResponse = RetrofitClient.api.predict(predictionReq)
                    if (predResponse.isSuccessful) {
                        val body = predResponse.body()
                        _state.update {
                            it.copy(
                                isApiLoading = false,
                                predictionLabel = body?.prediction_label,
                                predictionProbabilities = body?.probabilities ?: emptyMap()
                            )
                        }
                    } else {
                        val errorBody = predResponse.errorBody()?.string()
                        Log.e("DiagnosticViewModel", "API Error: ${predResponse.code()} - $errorBody")
                        _state.update {
                            it.copy(isApiLoading = false, apiError = "Prediction failed: HTTP ${predResponse.code()}")
                        }
                    }
                } else {
                    _state.update {
                        it.copy(isApiLoading = false, apiError = "Auth failed: HTTP ${tokenResponse.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("DiagnosticViewModel", "Network exception", e)
                _state.update {
                    it.copy(isApiLoading = false, apiError = "Network error: ${e.message}")
                }
            }
        }
    }
}
