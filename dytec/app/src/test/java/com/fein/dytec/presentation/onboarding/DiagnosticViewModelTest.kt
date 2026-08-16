package com.fein.dytec.presentation.onboarding

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiagnosticViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `reset should clear state and restart timer`() = runTest {
        val viewModel = DiagnosticViewModel()
        
        // Simulate a finished test
        viewModel.selectAnswer(1)
        viewModel.submitAnswer(true)
        
        // Assert state is changed
        val initialState = viewModel.state.value
        assertEquals(1, initialState.rawScore)
        
        // Action: reset
        viewModel.reset() 
        
        // The test will pass in GREEN phase
        val resetState = viewModel.state.value
        assertEquals("State should be reset to 0 score", 0, resetState.rawScore)
        assertEquals("State should have 0 elapsed time", 0, resetState.timeElapsedSeconds)
    }
}
