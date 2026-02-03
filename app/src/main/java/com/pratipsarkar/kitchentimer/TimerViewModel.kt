package com.pratipsarkar.kitchentimer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel: ViewModel() {
    // Private state
    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private var timerJob: Job? = null

    fun startTimer(seconds: Long) {
        if (_isRunning.value) return

        _timeLeft.value = seconds
        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (isActive && _timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
            _isRunning.value = false
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _isRunning.value = false
    }
}