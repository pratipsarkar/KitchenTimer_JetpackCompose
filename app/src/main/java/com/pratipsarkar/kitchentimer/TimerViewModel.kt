package com.pratipsarkar.kitchentimer

import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerViewModel(
    private val engine: TimerEngine = TimerEngine(),
    private val repository: TimerRepository,
): ViewModel() {

    private var _state =
        TimerState(
            durationMillis = 0L,
            startTimeMillis = null
        )
    private val _timeLeft = MutableStateFlow(0L)
    val timeLeft: StateFlow<Long> = _timeLeft

    private val _isRunning = MutableStateFlow(false)

    val isRunning: StateFlow<Boolean> = _isRunning

    private var _timerJob: Job? = null

    fun startTimer(seconds: Long) {
        if (_isRunning.value) return

        val now = System.currentTimeMillis()
        _state = TimerState(
            durationMillis = seconds * 1000,
            startTimeMillis = now
        )

        scheduleAndPersistTimer(now)

        _timeLeft.value = seconds
        _isRunning.value = true

        repository.startForegroundService()

        _timerJob?.cancel()
        _timerJob = viewModelScope.launch {
            while (isActive && _timeLeft.value > 0) {
                delay(1000)
                val remTimeInMillis = engine.remainingTime(_state, System.currentTimeMillis())
                _timeLeft.value = (remTimeInMillis + 999) / 1000

                if (_timeLeft.value <= 0) {
                    _isRunning.value = false
                    break
                }
            }
        }
    }

    fun stopTimer() {
        _timerJob?.cancel()
        _timerJob = null
        _timeLeft.value = 0
        _isRunning.value = false
        _state =
            TimerState(
                durationMillis = 0L,
                startTimeMillis = null
            )
        cancelTimer()
    }

    private fun scheduleAndPersistTimer(now: Long) {
        viewModelScope.launch {
            repository.saveTimer(now, _state.durationMillis)
            repository.scheduleAlarm(now + _state.durationMillis)
        }
    }

    private fun cancelTimer() {
        viewModelScope.launch {
            repository.cancelAlarm()
            repository.clearTimer()
        }
    }

}